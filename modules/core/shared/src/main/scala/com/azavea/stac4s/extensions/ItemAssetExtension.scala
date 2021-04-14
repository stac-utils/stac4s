package com.azavea.stac4s.extensions

import com.azavea.stac4s.StacAsset

import io.circe.syntax._
import io.circe.{Decoder, Encoder}

trait StacAssetExtension[T] {
  def getExtensionFields(asset: StacAsset): ExtensionResult[T]
  def addExtensionFields(asset: StacAsset, extensionFields: T): StacAsset
}

object StacAssetExtension {
  def apply[T](implicit ev: StacAssetExtension[T]): StacAssetExtension[T] = ev

  def instance[T](implicit decoder: Decoder[T], objectEncoder: Encoder.AsObject[T]) =
    new StacAssetExtension[T] {

      def getExtensionFields(asset: StacAsset): ExtensionResult[T] =
        decoder.decodeAccumulating(asset.extensionFields.asJson.hcursor)

      def addExtensionFields(asset: StacAsset, extensionFields: T): StacAsset =
        asset.copy(extensionFields = asset.extensionFields.deepMerge(objectEncoder.encodeObject(extensionFields)))
    }
}
