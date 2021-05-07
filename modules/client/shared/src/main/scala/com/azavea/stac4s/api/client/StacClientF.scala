package com.azavea.stac4s.api.client

import com.azavea.stac4s._

import eu.timepit.refined.types.string.NonEmptyString

trait StacClientF[F[_], S] {
  def search: F[List[StacItem]]
  def search(filter: S): F[List[StacItem]]
  def collections: F[List[StacCollection]]
  def collection(collectionId: NonEmptyString): F[StacCollection]
  def items(collectionId: NonEmptyString): F[List[StacItem]]
  def item(collectionId: NonEmptyString, itemId: NonEmptyString): F[StacItem]
  def itemCreate(collectionId: NonEmptyString, item: StacItem): F[StacItem]
  def collectionCreate(collection: StacCollection): F[StacCollection]
}
