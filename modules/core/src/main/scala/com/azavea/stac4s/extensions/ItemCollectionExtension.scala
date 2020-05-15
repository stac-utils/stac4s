package com.azavea.stac4s.extensions

import com.azavea.stac4s._

import io.circe.{Decoder, Encoder}
import io.circe.syntax._

trait ItemCollectionExtension[T] {
  def getProperties(itemCollection: ItemCollection): ExtensionResult[T]
  def extend(itemCollection: ItemCollection, properties: T): ItemCollection
}

object ItemExtensionCollection {
  def apply[T](implicit ev: ItemCollectionExtension[T]): ItemCollectionExtension[T] = ev

  def instance[T](implicit decoder: Decoder[T], objectEncoder: Encoder.AsObject[T]): ItemCollectionExtension[T] =
    new ItemCollectionExtension[T] {

      def getProperties(itemCollection: ItemCollection): ExtensionResult[T] =
        decoder.decodeAccumulating(
          itemCollection.extensionFields.asJson.hcursor
        )

      def extend(itemCollection: ItemCollection, extensionProperties: T) =
        itemCollection.copy(extensionFields =
          itemCollection.extensionFields.deepMerge(objectEncoder.encodeObject(extensionProperties))
        )
    }
}
