package com.azavea.stac4s.api.client

import com.azavea.stac4s.{StacCollection, StacItem}

import cats.MonadError
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.option._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.syntax._
import io.circe.{Encoder, Json}
import sttp.client3.circe.asJson
import sttp.client3.{SttpBackend, basicRequest}
import sttp.model.Uri

abstract class SttpStacClientF[F[_]: MonadError[*[_], Throwable]](
    client: SttpBackend[F, Any],
    baseUri: Uri
) extends StacClient[F] {

  type Filter

  protected implicit def filterEncoder: Encoder[Filter]

  def search: F[List[StacItem]] = search(None)

  def search(filter: Filter): F[List[StacItem]] = search(filter.asJson.some)

  private def search(filter: Option[Json]): F[List[StacItem]] =
    client
      .send {
        filter
          .fold(basicRequest)(f => basicRequest.body(f.asJson.noSpaces))
          .post(baseUri.withPath("search"))
          .response(asJson[Json])
      }
      .map(_.body.flatMap(_.hcursor.downField("features").as[List[StacItem]]))
      .flatMap(MonadError[F, Throwable].fromEither)

  def collections: F[List[StacCollection]] =
    client
      .send(basicRequest.get(baseUri.withPath("collections")).response(asJson[Json]))
      .map(_.body.flatMap(_.hcursor.downField("collections").as[List[StacCollection]]))
      .flatMap(MonadError[F, Throwable].fromEither)

  def collection(collectionId: NonEmptyString): F[StacCollection] =
    client
      .send(
        basicRequest
          .get(baseUri.withPath("collections", collectionId.value))
          .response(asJson[StacCollection])
      )
      .map(_.body)
      .flatMap(MonadError[F, Throwable].fromEither)

  def items(collectionId: NonEmptyString): F[List[StacItem]] =
    client
      .send(basicRequest.get(baseUri.withPath("collections", collectionId.value, "items")).response(asJson[Json]))
      .map(_.body.flatMap(_.hcursor.downField("features").as[List[StacItem]]))
      .flatMap(MonadError[F, Throwable].fromEither)

  def item(collectionId: NonEmptyString, itemId: NonEmptyString): F[StacItem] =
    client
      .send(
        basicRequest
          .get(baseUri.withPath("collections", collectionId.value, "items", itemId.value))
          .response(asJson[StacItem])
      )
      .map(_.body)
      .flatMap(MonadError[F, Throwable].fromEither)

  def itemCreate(collectionId: NonEmptyString, item: StacItem): F[StacItem] =
    client
      .send(
        basicRequest
          .post(baseUri.withPath("collections", collectionId.value, "items"))
          .body(item.asJson.noSpaces)
          .response(asJson[StacItem])
      )
      .map(_.body)
      .flatMap(MonadError[F, Throwable].fromEither)

  def collectionCreate(collection: StacCollection): F[StacCollection] =
    client
      .send(
        basicRequest
          .post(baseUri.withPath("collections"))
          .body(collection.asJson.noSpaces)
          .response(asJson[StacCollection])
      )
      .map(_.body)
      .flatMap(MonadError[F, Throwable].fromEither)
}

object SttpStacClientF {
  type Aux[F[_], S] = SttpStacClientF[F] { type Filter = S }

  def instance[F[_]: MonadError[*[_], Throwable], S](
      client: SttpBackend[F, Any],
      baseUri: Uri
  )(implicit sencoder: Encoder[S]): Aux[F, S] = new SttpStacClientF[F](client, baseUri) {
    type Filter = S
    protected val filterEncoder: Encoder[Filter] = sencoder
  }
}
