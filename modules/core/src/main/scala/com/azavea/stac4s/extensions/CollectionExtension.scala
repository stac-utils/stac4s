package com.azavea.stac4s.extensions

import com.azavea.stac4s.StacCollection

import io.circe.{Decoder, Encoder}
import io.circe.syntax._

trait CollectionExtension[T] {
  def getProperties(collection: StacCollection): ExtensionResult[T]

  def extend(collection: StacCollection, extensionFields: T): StacCollection
}

object CollectionExtension {
  def apply[T](implicit ev: CollectionExtension[T]): CollectionExtension[T] = ev

  def instance[T](implicit decoder: Decoder[T], objectEncoder: Encoder.AsObject[T]) =
    new CollectionExtension[T] {

      def getProperties(collection: StacCollection): ExtensionResult[T] =
        decoder.decodeAccumulating(collection.extensionFields.asJson.hcursor)

      def extend(collection: StacCollection, extensionFields: T): StacCollection =
        collection.copy(extensionFields = collection.extensionFields.deepMerge(objectEncoder.encodeObject(extensionFields)))
    }
}