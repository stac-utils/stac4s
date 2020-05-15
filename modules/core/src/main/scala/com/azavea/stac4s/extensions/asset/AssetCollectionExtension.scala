package com.azavea.stac4s.extensions.asset

import com.azavea.stac4s.extensions.CollectionExtension

import io.circe._
import io.circe.syntax._

final case class AssetCollectionExtension(
    assets: Map[String, StacCollectionAsset]
)

object AssetCollectionExtension {

  implicit val encAssetCollectionExtension: Encoder.AsObject[AssetCollectionExtension] = Encoder
    .AsObject[Map[String, Json]]
    .contramapObject((extensionFields: AssetCollectionExtension) => Map("assets" -> extensionFields.assets.asJson))

  implicit val decAssetCollectionExtension: Decoder[AssetCollectionExtension] =
    Decoder.forProduct1("assets")(AssetCollectionExtension.apply)

  implicit val collectionExtension: CollectionExtension[AssetCollectionExtension] =
    CollectionExtension.instance
}
