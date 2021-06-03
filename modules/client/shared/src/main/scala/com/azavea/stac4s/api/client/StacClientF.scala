package com.azavea.stac4s.api.client

import com.azavea.stac4s.{StacCollection, StacItem}

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Json

trait StacClientF[F[_], S] {
  def collection(collectionId: NonEmptyString): F[StacCollection]
  def collectionCreate(collection: StacCollection): F[StacCollection]
  def item(collectionId: NonEmptyString, itemId: NonEmptyString): F[ETag[StacItem]]
  def itemCreate(collectionId: NonEmptyString, item: StacItem): F[ETag[StacItem]]
  def itemUpdate(collectionId: NonEmptyString, item: ETag[StacItem]): F[ETag[StacItem]]
  def itemPatch(collectionId: NonEmptyString, itemId: NonEmptyString, patch: ETag[Json]): F[ETag[StacItem]]
  def itemDelete(collectionId: NonEmptyString, itemId: NonEmptyString): F[Either[String, String]]
}

trait StreamingStacClientF[F[_], G[_], S] extends StacClientF[F, S] {
  def collections: G[StacCollection]
  def search: G[StacItem]
  def search(filter: S): G[StacItem]
  def items(collectionId: NonEmptyString): G[StacItem]
}
