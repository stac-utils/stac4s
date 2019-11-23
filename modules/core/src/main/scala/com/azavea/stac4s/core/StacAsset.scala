package com.azavea.stac4s.core

import io.circe._

final case class StacAsset(
    href: String,
    title: Option[String],
    _type: Option[StacMediaType]
)

object StacAsset {

  implicit val encStacAsset: Encoder[StacAsset] =
    Encoder.forProduct3("href", "title", "type")(
      asset => (asset.href, asset.title, asset._type)
    )

  implicit val decStacAsset: Decoder[StacAsset] =
    Decoder.forProduct3("href", "title", "type")(
      StacAsset.apply _
    )
}
