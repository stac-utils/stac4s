package com.azavea.stac4s.core

import cats.Eq
import io.circe._

final case class StacCollectionAsset(
    title: String,
    description: Option[String],
    roles: List[StacAssetRole],
    _type: StacMediaType
)

object StacCollectionAsset {

  implicit val eqStacCollectionAsset: Eq[StacCollectionAsset] = Eq.fromUniversalEquals

  implicit val encStacCollectionAsset: Encoder[StacCollectionAsset] =
    Encoder.forProduct4("title", "description", "roles", "type")(asset =>
      (asset.title, asset.description, asset.roles, asset._type)
    )

  implicit val decStacCollectionAsset: Decoder[StacCollectionAsset] =
    Decoder.forProduct4("title", "description", "roles", "type")(
      StacCollectionAsset.apply
    )
}
