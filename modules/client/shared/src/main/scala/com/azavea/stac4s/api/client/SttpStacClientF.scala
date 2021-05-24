package com.azavea.stac4s.api.client

import com.azavea.stac4s.{StacCollection, StacItem, StacLink, StacLinkType}

import cats.MonadThrow
import cats.syntax.apply._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.option._
import eu.timepit.refined.types.string.NonEmptyString
import fs2.Stream
import io.circe.syntax._
import io.circe.{Encoder, Error, Json, JsonObject}
import monocle.Lens
import sttp.client3.circe.asJson
import sttp.client3.{ResponseException, SttpBackend, UriContext, basicRequest}
import sttp.model.Uri

case class SttpStacClientF[F[_]: MonadThrow, S: Lens[*, Option[PaginationToken]]: Encoder](
    client: SttpBackend[F, Any],
    baseUri: Uri
) extends StreamingStacClientF[F, Stream[F, *], S] {
  private val paginationTokenLens = implicitly[Lens[S, Option[PaginationToken]]]

  /** Get the next page [[Uri]] from the retrieved [[Json]] body. */
  private def getNextLink(body: Either[ResponseException[String, Error], Json]): F[Option[Uri]] =
    body
      .flatMap {
        _.hcursor
          .downField("links")
          .as[Option[List[StacLink]]]
          .map(_.flatMap(_.collectFirst { case l if l.rel == StacLinkType.Next => uri"${l.href}" }))
      }
      .liftTo[F]

  def search: Stream[F, StacItem] = search(None)

  def search(filter: S): Stream[F, StacItem] = search(filter.some)

  private def search(filter: Option[S]): Stream[F, StacItem] = {
    val emptyJson = JsonObject.empty.asJson
    // the initial filter may contain the paginationToken that is used for the initial query
    val initialBody = filter.map(_.asJson).getOrElse(emptyJson)
    // the same filter would be used as a body for all pagination requests
    val noPaginationBody = filter.map(paginationTokenLens.set(None)(_).asJson).getOrElse(emptyJson)
    Stream
      .unfoldLoopEval((baseUri.withPath("search"), initialBody)) { case (link, request) =>
        client
          .send(basicRequest.body(request.noSpaces).post(link).response(asJson[Json]))
          .flatMap { response =>
            val body  = response.body
            val items = body.flatMap(_.hcursor.downField("features").as[List[StacItem]]).liftTo[F]
            val next  = getNextLink(body).map(_.map(_ -> noPaginationBody))
            (items, next).tupled
          }
      }
      .flatMap(Stream.emits)
  }

  def collections: Stream[F, StacCollection] =
    Stream
      .unfoldLoopEval(baseUri.withPath("collections")) { link =>
        client
          .send(basicRequest.get(link).response(asJson[Json]))
          .flatMap { response =>
            val body     = response.body
            val items    = body.flatMap(_.hcursor.downField("collections").as[List[StacCollection]]).liftTo[F]
            val nextLink = getNextLink(body)

            (items, nextLink).tupled
          }
      }
      .flatMap(Stream.emits)

  def collection(collectionId: NonEmptyString): F[StacCollection] =
    client
      .send(
        basicRequest
          .get(baseUri.withPath("collections", collectionId.value))
          .response(asJson[StacCollection])
      )
      .flatMap(_.body.liftTo[F])

  def items(collectionId: NonEmptyString): Stream[F, StacItem] = {
    Stream
      .unfoldLoopEval(baseUri.withPath("collections", collectionId.value, "items")) { link =>
        client
          .send(basicRequest.get(link).response(asJson[Json]))
          .flatMap { response =>
            val body     = response.body
            val items    = body.flatMap(_.hcursor.downField("features").as[List[StacItem]]).liftTo[F]
            val nextLink = getNextLink(body)

            (items, nextLink).tupled
          }
      }
      .flatMap(Stream.emits)
  }

  def item(collectionId: NonEmptyString, itemId: NonEmptyString): F[StacItem] =
    client
      .send(
        basicRequest
          .get(baseUri.withPath("collections", collectionId.value, "items", itemId.value))
          .response(asJson[StacItem])
      )
      .flatMap(_.body.liftTo[F])

  def itemCreate(collectionId: NonEmptyString, item: StacItem): F[StacItem] =
    client
      .send(
        basicRequest
          .post(baseUri.withPath("collections", collectionId.value, "items"))
          .body(item.asJson.noSpaces)
          .response(asJson[StacItem])
      )
      .flatMap(_.body.liftTo[F])

  def collectionCreate(collection: StacCollection): F[StacCollection] =
    client
      .send(
        basicRequest
          .post(baseUri.withPath("collections"))
          .body(collection.asJson.noSpaces)
          .response(asJson[StacCollection])
      )
      .flatMap(_.body.liftTo[F])
}
