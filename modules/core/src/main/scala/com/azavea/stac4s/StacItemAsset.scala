package com.azavea.stac4s

import cats.Eq
import io.circe._

final case class StacItemAsset(
    href: String,
    title: Option[String],
    description: Option[String],
    roles: List[StacAssetRole],
    _type: Option[StacMediaType]
)

object StacItemAsset {

  implicit val eqStacItemAsset: Eq[StacItemAsset] = Eq.fromUniversalEquals

  implicit val encStacItemAsset: Encoder[StacItemAsset] =
    Encoder.forProduct5("href", "title", "description", "roles", "type")(asset =>
      (asset.href, asset.title, asset.description, asset.roles, asset._type)
    )

  implicit val decStacItemAsset: Decoder[StacItemAsset] =
    Decoder.forProduct5("href", "title", "description", "roles", "type")(
      StacItemAsset.apply
    )
}
