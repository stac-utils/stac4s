package com.azavea.stac4s.api.client

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
import io.circe.syntax._
import io.circe.{Encoder, Json, JsonObject}
import monocle.Lens
import sttp.client3.circe.asJson
import sttp.client3.{Response, SttpBackend, UriContext, basicRequest}
import sttp.model.Uri

case class SttpStacClientF[F[_]: MonadThrow, S: Lens[*, Option[PaginationToken]]: Encoder](
    client: SttpBackend[F, Any],
    baseUri: Uri
) extends StreamingStacClientF[F, Stream[F, *], S] {
  import SttpStacClientF._

  private val paginationTokenLens = implicitly[Lens[S, Option[PaginationToken]]]

  def search: Stream[F, StacItem] = search(None)

  def search(filter: S): Stream[F, StacItem] = search(filter.some)

  private def search(filter: Option[S]): Stream[F, StacItem] = {
    val emptyJson = JsonObject.empty.asJson
    // the initial filter may contain the paginationToken that is used for the initial query
    val initialBody = filter.map(_.asJson).getOrElse(emptyJson)
    // the same filter would be used as a body for all pagination requests
    val noPaginationBody = filter.map(paginationTokenLens.set(None)(_).asJson).getOrElse(emptyJson)
    Stream
      .unfoldLoopEval((baseUri.addPath("search"), initialBody)) { case (link, request) =>
        client
          .send(basicRequest.body(request.noSpaces).post(link).response(asJson[Json]))
          .flatMap { response =>
            val items = response.stacItems
            val next  = response.nextLink.nested.map(_ -> noPaginationBody).value
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

  implicit class ResponseEitherJsonOps[E <: Exception](val self: Response[Either[E, Json]]) extends AnyVal {

    /** Get the next page Uri from the retrieved Json body. */
    def nextLink[F[_]: MonadThrow]: F[Option[Uri]] =
      self.body
        .flatMap {
          _.hcursor
            .downField("links")
            .as[Option[List[StacLink]]]
            .map(_.flatMap(_.collectFirst { case l if l.rel == StacLinkType.Next => uri"${l.href}" }))
        }
        .liftTo[F]

    /** Decode List of StacItem from the retrieved Json body. */
    def stacItems[F[_]: MonadThrow]: F[List[StacItem]] =
      self.body.flatMap(_.hcursor.downField("features").as[List[StacItem]]).liftTo[F]

    /** Decode List of StacCollections from the retrieved Json body. */
    def stacCollections[F[_]: MonadThrow]: F[List[StacCollection]] =
      self.body.flatMap(_.hcursor.downField("collections").as[List[StacCollection]]).liftTo[F]
  }
}
