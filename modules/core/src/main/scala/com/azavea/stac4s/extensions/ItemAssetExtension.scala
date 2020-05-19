package com.azavea.stac4s.extensions

import com.azavea.stac4s.StacItemAsset

import io.circe.{Decoder, Encoder}
import io.circe.syntax._

trait ItemAssetExtension[T] {
  def getExtensionFields(asset: StacItemAsset): ExtensionResult[T]
  def extend(asset: StacItemAsset, extensionFields: T): StacItemAsset
}

object ItemAssetExtension {
  def apply[T](implicit ev: ItemAssetExtension[T]): ItemAssetExtension[T] = ev

  def instance[T](implicit decoder: Decoder[T], objectEncoder: Encoder.AsObject[T]) =
    new ItemAssetExtension[T] {

      def getExtensionFields(asset: StacItemAsset): ExtensionResult[T] =
        decoder.decodeAccumulating(asset.extensionFields.asJson.hcursor)

      def extend(asset: StacItemAsset, extensionFields: T): StacItemAsset =
        asset.copy(extensionFields = asset.extensionFields.deepMerge(objectEncoder.encodeObject(extensionFields)))
    }
}
