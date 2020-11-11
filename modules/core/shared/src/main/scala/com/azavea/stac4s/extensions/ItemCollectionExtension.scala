package com.azavea.stac4s.extensions

import com.azavea.stac4s._

import io.circe.syntax._
import io.circe.{Decoder, Encoder}

trait ItemCollectionExtension[T] {
  def getExtensionFields(itemCollection: ItemCollection): ExtensionResult[T]
  def addExtensionFields(itemCollection: ItemCollection, properties: T): ItemCollection
}

object ItemExtensionCollection {
  def apply[T](implicit ev: ItemCollectionExtension[T]): ItemCollectionExtension[T] = ev

  def instance[T](implicit decoder: Decoder[T], objectEncoder: Encoder.AsObject[T]): ItemCollectionExtension[T] =
    new ItemCollectionExtension[T] {

      def getExtensionFields(itemCollection: ItemCollection): ExtensionResult[T] =
        decoder.decodeAccumulating(
          itemCollection.extensionFields.asJson.hcursor
        )

      def addExtensionFields(itemCollection: ItemCollection, extensionProperties: T): ItemCollection =
        itemCollection.copy(extensionFields =
          itemCollection.extensionFields.deepMerge(objectEncoder.encodeObject(extensionProperties))
        )
    }
}
