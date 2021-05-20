package com.azavea.stac4s.api.client

import com.azavea.stac4s._

import eu.timepit.refined.types.string.NonEmptyString

trait StacClientF[F[_], S] {
  def collection(collectionId: NonEmptyString): F[StacCollection]
  def item(collectionId: NonEmptyString, itemId: NonEmptyString): F[StacItem]
  def itemCreate(collectionId: NonEmptyString, item: StacItem): F[StacItem]
  def collectionCreate(collection: StacCollection): F[StacCollection]
}

trait StreamingStacClientF[F[_], G[_], S] extends StacClientF[F, S] {
  def search: G[StacItem]
  def search(filter: S): G[StacItem]
  def collections: G[StacCollection]
  def items(collectionId: NonEmptyString): G[StacItem]
}
