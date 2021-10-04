package com.azavea.stac4s.api.client

import com.azavea.stac4s.api.client.SttpStacClientF.PaginationToken
import com.azavea.stac4s.api.client.util.syntax._
import com.azavea.stac4s.{StacCollection, StacItem, StacLink, StacLinkType}

import cats.MonadThrow
import cats.syntax.apply._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.nested._
import cats.syntax.option._
import eu.timepit.refined.types.string.NonEmptyString
import fs2.Stream
import io.circe.refined._
import io.circe.syntax._
import io.circe.{Encoder, Json, JsonObject}
import monocle.Lens
import sttp.client3.circe.asJson
import sttp.client3.{Response, SttpBackend, UriContext, basicRequest}
import sttp.model.{MediaType, Uri}

case class SttpStacClientF[F[_]: MonadThrow, S: Lens[*, Option[PaginationToken]]: Encoder](
    client: SttpBackend[F, Any],
    baseUri: Uri
) extends StreamingStacClientF[F, Stream[F, *], S] {
  import SttpStacClientF._

  def search: Stream[F, StacItem] = search(None)

  def search(filter: S): Stream[F, StacItem] = search(filter.some)

  def search(filter: Option[S]): Stream[F, StacItem] = {
    val emptyJson = JsonObject.empty.asJson
    // the initial filter may contain the paginationToken that is used for the initial query
    val initialBody = filter.map(_.asJson).getOrElse(emptyJson)
    Stream
      .unfoldLoopEval((baseUri.addPath("search"), initialBody)) { case (link, request) =>
        client
          .send(
            basicRequest
              .post(link)
              .contentType(MediaType.ApplicationJson)
              .body(request.deepDropNullValues.noSpaces)
              .response(asJson[Json])
          )
          .flatMap { response =>
            val items = response.stacItems
            val next  = response.nextPage(filter)
            (items, next).tupled
          }
      }
      .flatMap(Stream.emits)
  }

  def collections: Stream[F, StacCollection] =
    Stream
      .unfoldLoopEval(baseUri.addPath("collections")) { link =>
        client
          .send(basicRequest.get(link).response(asJson[Json]))
          .flatMap { response =>
            val items    = response.stacCollections
            val nextLink = response.nextLink
            (items, nextLink).tupled
          }
      }
      .flatMap(Stream.emits)

  def collection(collectionId: NonEmptyString): F[StacCollection] =
    client
      .send(
        basicRequest
          .get(baseUri.addPath("collections", collectionId.value))
          .response(asJson[StacCollection])
      )
      .flatMap(_.body.liftTo[F])

  def collectionCreate(collection: StacCollection): F[StacCollection] =
    client
      .send(
        basicRequest
          .post(baseUri.addPath("collections"))
          .contentType(MediaType.ApplicationJson)
          .body(collection.asJson.noSpaces)
          .response(asJson[StacCollection])
      )
      .flatMap(_.body.liftTo[F])

  def items(collectionId: NonEmptyString): Stream[F, StacItem] =
    Stream
      .unfoldLoopEval(baseUri.addPath("collections", collectionId.value, "items")) { link =>
        client
          .send(basicRequest.get(link).response(asJson[Json]))
          .flatMap { response =>
            val items    = response.stacItems
            val nextLink = response.nextLink
            (items, nextLink).tupled
          }
      }
      .flatMap(Stream.emits)

  def item(collectionId: NonEmptyString, itemId: NonEmptyString): F[ETag[StacItem]] =
    client
      .send(
        basicRequest
          .get(baseUri.addPath("collections", collectionId.value, "items", itemId.value))
          .response(asJson[StacItem])
      )
      .flatMap(_.bodyETag.liftTo[F])

  def itemCreate(collectionId: NonEmptyString, item: StacItem): F[ETag[StacItem]] =
    client
      .send(
        basicRequest
          .post(baseUri.addPath("collections", collectionId.value, "items"))
          .contentType(MediaType.ApplicationJson)
          .body(item.asJson.noSpaces)
          .response(asJson[StacItem])
      )
      .flatMap(_.bodyETag.liftTo[F])

  def itemUpdate(collectionId: NonEmptyString, item: ETag[StacItem]): F[ETag[StacItem]] =
    client
      .send(
        basicRequest
          .put(baseUri.addPath("collections", collectionId.value, "items", item.entity.id))
          .headerIfMatch(item.tag)
          .body(item.entity.asJson.noSpaces)
          .response(asJson[StacItem])
      )
      .flatMap(_.bodyETag.liftTo[F])

  def itemPatch(collectionId: NonEmptyString, itemId: NonEmptyString, patch: ETag[Json]): F[ETag[StacItem]] =
    client
      .send(
        basicRequest
          .patch(baseUri.addPath("collections", collectionId.value, "items", itemId.value))
          .headerIfMatch(patch.tag)
          .body(patch.entity.noSpaces)
          .response(asJson[StacItem])
      )
      .flatMap(_.bodyETag.liftTo[F])

  def itemDelete(collectionId: NonEmptyString, itemId: NonEmptyString): F[Either[String, String]] =
    client
      .send(basicRequest.delete(baseUri.addPath("collections", collectionId.value, "items", itemId.value)))
      .map(_.body)
}

object SttpStacClientF {
  // TODO: should be a newtype
  type PaginationToken = NonEmptyString

  implicit class ResponseEitherJsonOps[E <: Exception](val self: Response[Either[E, Json]]) extends AnyVal {

    /** Get the next page [[Uri]] from the retrieved [[Json]] body and the next [[PaginationToken]]. */
    def nextPage[F[_]: MonadThrow]: F[Option[(Uri, Option[PaginationToken])]] =
      self.body
        .flatMap {
          _.hcursor
            .downField("links")
            .as[Option[List[StacLink]]]
            .map(_.flatMap(_.collectFirst {
              case l if l.rel == StacLinkType.Next =>
                // The STAC API server may return the next page token as a part of the extensionFields,
                // it is just a string that should be used in the next page request body.
                // Some STAC API implementations (i.e. Franklin)
                // encode pagination into the next page Uri (put it into the l.href):
                // in this case, the pagination token is always set to None and only Uri is used for the pagination purposes.
                val paginationToken: Option[PaginationToken] =
                  l
                    .extensionFields("body")
                    .flatMap(_.asObject)
                    .flatMap(_("next"))
                    .flatMap(_.as[PaginationToken].toOption)

                uri"${l.href}" -> paginationToken
            }))
        }
        .liftTo[F]

    /** Get the next page [[Uri]] and the next page [[Json]] request body (that has a correctly set next page token). */
    def nextPage[F[_]: MonadThrow, S](
        filter: Option[S]
    )(implicit l: Lens[S, Option[PaginationToken]], enc: Encoder[S]): F[Option[(Uri, Json)]] =
      nextPage.nested.map { case (uri, token) => (uri, filter.setPaginationToken(token)) }.value

    /** Get the next page [[Uri]] and drop the next page token / body. Useful for get requests with no POST pagination
      * support.
      */
    def nextLink[F[_]: MonadThrow]: F[Option[Uri]] = nextPage.nested.map(_._1).value

    /** Decode List of [[StacItem]] from the retrieved [[Json]] body. */
    def stacItems[F[_]: MonadThrow]: F[List[StacItem]] =
      self.body.flatMap(_.hcursor.downField("features").as[List[StacItem]]).liftTo[F]

    /** Decode List of [[StacCollection]] from the retrieved [[Json]] body. */
    def stacCollections[F[_]: MonadThrow]: F[List[StacCollection]] =
      self.body.flatMap(_.hcursor.downField("collections").as[List[StacCollection]]).liftTo[F]
  }

  implicit class StacFilterOps[S](val self: Option[S]) extends AnyVal {

    def setPaginationToken(
        token: Option[PaginationToken]
    )(implicit l: Lens[S, Option[PaginationToken]], enc: Encoder[S]): Json =
      self.map(l.set(token)(_).asJson).getOrElse(JsonObject.empty.asJson)
  }
}
