package com.azavea.stac4s.api.client

import com.azavea.stac4s._

import eu.timepit.refined.types.string.NonEmptyString
import fs2.Stream

trait StacClientF[F[_], S] {
  def search: Stream[F, StacItem]
  def search(filter: S): Stream[F, StacItem]
  def collections: Stream[F, StacCollection]
  def collection(collectionId: NonEmptyString): F[StacCollection]
  def items(collectionId: NonEmptyString): Stream[F, StacItem]
  def item(collectionId: NonEmptyString, itemId: NonEmptyString): F[StacItem]
  def itemCreate(collectionId: NonEmptyString, item: StacItem): F[StacItem]
  def collectionCreate(collection: StacCollection): F[StacCollection]
}
