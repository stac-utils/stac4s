package com.azavea.stac4s.extensions

import com.azavea.stac4s.StacItemAsset

import io.circe.{Decoder, Encoder}
import io.circe.syntax._

trait AssetExtension[T] {
  def getProperties(asset: StacItemAsset): ExtensionResult[T]
  def extend(asset: StacItemAsset, extensionFields: T): StacItemAsset
}

object AssetExtension {
  def apply[T](implicit ev: AssetExtension[T]): AssetExtension[T] = ev

  def instance[T](implicit decoder: Decoder[T], objectEncoder: Encoder.AsObject[T]) =
    new AssetExtension[T] {

      def getProperties(asset: StacItemAsset): ExtensionResult[T] =
        decoder.decodeAccumulating(asset.extensionFields.asJson.hcursor)

      def extend(asset: StacItemAsset, extensionFields: T): StacItemAsset =
        asset.copy(extensionFields = asset.extensionFields.deepMerge(objectEncoder.encodeObject(extensionFields)))
    }
}
