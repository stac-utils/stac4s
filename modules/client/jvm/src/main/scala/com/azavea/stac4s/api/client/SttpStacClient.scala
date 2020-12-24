package com.azavea.stac4s.api.client

import com.azavea.stac4s.{StacCollection, StacItem}

import cats.MonadError
import cats.syntax.flatMap._
import cats.syntax.functor._
import eu.timepit.refined.types.string.NonEmptyString
import io.chrisdavenport.log4cats.Logger
import io.circe.Json
import io.circe.syntax._
import sttp.client3._
import sttp.client3.circe.asJson
import sttp.model.Uri

case class SttpStacClient[F[_]: MonadError[*[_], Throwable]: Logger](
    client: SttpBackend[F, Any],
    baseUri: Uri
) extends StacClient[F] {

  type Filter = SearchFilters

  def search(filter: SearchFilters = SearchFilters()): F[List[StacItem]] =
    Logger[F].trace(s"search: ${filter.asJson.spaces2}") >>
      client
        .send(basicRequest.post(baseUri.withPath("search")).body(filter.asJson.noSpaces).response(asJson[Json]))
        .map(_.body.flatMap(_.hcursor.downField("features").as[List[StacItem]]))
        .flatMap(MonadError[F, Throwable].fromEither)

  def collections: F[List[StacCollection]] =
    Logger[F].trace("collections") >>
      client
        .send(basicRequest.get(baseUri.withPath("collections")).response(asJson[Json]))
        .map(_.body.flatMap(_.hcursor.downField("collections").as[List[StacCollection]]))
        .flatMap(MonadError[F, Throwable].fromEither)

  def collection(collectionId: NonEmptyString): F[Option[StacCollection]] =
    Logger[F].trace(s"collection: $collectionId") >>
      client
        .send(
          basicRequest
            .get(baseUri.withPath("collections", collectionId.value))
            .response(asJson[Option[StacCollection]])
        )
        .map(_.body)
        .flatMap(MonadError[F, Throwable].fromEither)

  def items(collectionId: NonEmptyString): F[List[StacItem]] =
    Logger[F].trace(s"items: $collectionId") >>
      client
        .send(basicRequest.get(baseUri.withPath("collections", collectionId.value, "items")).response(asJson[Json]))
        .map(_.body.flatMap(_.hcursor.downField("features").as[List[StacItem]]))
        .flatMap(MonadError[F, Throwable].fromEither)

  def item(collectionId: NonEmptyString, itemId: NonEmptyString): F[Option[StacItem]] =
    Logger[F].trace(s"items: $collectionId, $itemId") >>
      client
        .send(
          basicRequest
            .get(baseUri.withPath("collections", collectionId.value, "items", itemId.value))
            .response(asJson[Option[StacItem]])
        )
        .map(_.body)
        .flatMap(MonadError[F, Throwable].fromEither)

  def itemCreate(collectionId: NonEmptyString, item: StacItem): F[StacItem] =
    Logger[F].trace(s"createItem: ($collectionId, ${item.asJson.spaces2})") >>
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
    Logger[F].trace(s"createCollection: ${collection.asJson.spaces2}") >>
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
