package com.azavea.stac4s.extensions

import com.azavea.stac4s.StacItem

import io.circe._
import io.circe.syntax._

// typeclass trait for anything that is an extension of item properties
trait ItemExtension[T] {
  def getExtensionFields(item: StacItem): ExtensionResult[T]
  def addExtensionFields(item: StacItem, properties: T): StacItem
}

object ItemExtension {
  // summoner
  def apply[T](implicit ev: ItemExtension[T]): ItemExtension[T] = ev

  // constructor for anything with a `Decoder` and an `Encoder.AsObject`
  def instance[T](implicit decoder: Decoder[T], objectEncoder: Encoder.AsObject[T]): ItemExtension[T] =
    new ItemExtension[T] {

      def getExtensionFields(item: StacItem): ExtensionResult[T] =
        decoder.decodeAccumulating(
          item.properties.asJson.hcursor
        )

      def addExtensionFields(item: StacItem, extensionProperties: T) =
        item.copy(properties = item.properties.deepMerge(objectEncoder.encodeObject(extensionProperties)))
    }
}
