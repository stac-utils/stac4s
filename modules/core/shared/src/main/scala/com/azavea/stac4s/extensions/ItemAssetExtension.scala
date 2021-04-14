package com.azavea.stac4s.extensions

import com.azavea.stac4s.ItemAsset

import io.circe.syntax._
import io.circe.{Decoder, Encoder}

trait ItemAssetExtension[T] {
  def getExtensionFields(asset: ItemAsset): ExtensionResult[T]
  def addExtensionFields(asset: ItemAsset, extensionFields: T): ItemAsset
}

object ItemAssetExtension {
  def apply[T](implicit ev: ItemAssetExtension[T]): ItemAssetExtension[T] = ev

  def instance[T](implicit decoder: Decoder[T], objectEncoder: Encoder.AsObject[T]) =
    new ItemAssetExtension[T] {

      def getExtensionFields(asset: ItemAsset): ExtensionResult[T] =
        decoder.decodeAccumulating(asset.extensionFields.asJson.hcursor)

      def addExtensionFields(asset: ItemAsset, extensionFields: T): ItemAsset =
        asset.copy(extensionFields = asset.extensionFields.deepMerge(objectEncoder.encodeObject(extensionFields)))
    }
}
