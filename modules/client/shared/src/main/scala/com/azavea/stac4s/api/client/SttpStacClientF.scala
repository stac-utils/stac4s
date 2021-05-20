package com.azavea.stac4s.api.client

import com.azavea.stac4s.{StacCollection, StacItem, StacLink, StacLinkType}

import cats.MonadThrow
import cats.syntax.apply._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.option._
import eu.timepit.refined.types.string.NonEmptyString
import fs2.Stream
import io.circe
import io.circe.syntax._
import io.circe.{Encoder, Json}
import sttp.client3.circe.asJson
import sttp.client3.{ResponseException, SttpBackend, UriContext, basicRequest}
import sttp.model.Uri

case class SttpStacClientF[F[_]: MonadThrow, S: Encoder](
    client: SttpBackend[F, Any],
    baseUri: Uri
) extends StacClientF[F, S] {

  /** Get the next page [[Uri]] from the received [[Json]] body. */
  private def getNextLink(body: Either[ResponseException[String, circe.Error], Json]): F[Option[Uri]] =
    body
      .flatMap {
        _.hcursor
          .downField("links")
          .as[Option[List[StacLink]]]
          .map(_.flatMap(_.collectFirst { case l if l.rel == StacLinkType.Next => uri"${l.href}" }))
      }
      .liftTo[F]

  def search: Stream[F, StacItem] = search(None)

  def search(filter: S): Stream[F, StacItem] = search(filter.asJson.some)

  private def search(filter: Option[Json]): Stream[F, StacItem] =
    Stream
      .unfoldLoopEval(baseUri.withPath("search")) { link =>
        client
          .send(filter.fold(basicRequest)(f => basicRequest.body(f.asJson.noSpaces)).post(link).response(asJson[Json]))
          .flatMap { response =>
            val body     = response.body
            val items    = body.flatMap(_.hcursor.downField("features").as[List[StacItem]]).liftTo[F]
            val nextLink = getNextLink(body)

            (items, nextLink).tupled
          }
      }
      .flatMap(Stream.emits)

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
