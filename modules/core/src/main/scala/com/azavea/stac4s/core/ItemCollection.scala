package com.azavea.stac4s

import cats.Eq
import io.circe._
import io.circe.refined._

final case class ItemCollection(
    _type: String = "FeatureCollection",
    stacVersion: StacVersion,
    stacExtensions: List[String],
    features: List[StacItem],
    links: List[StacLink]
)

object ItemCollection {

  implicit val eqItemCollection: Eq[ItemCollection] = Eq.fromUniversalEquals

  implicit val encItemCollection: Encoder[ItemCollection] = Encoder.forProduct5(
    "type",
    "stac_version",
    "stac_extensions",
    "features",
    "links"
  )(
    itemCollection =>
      (
        itemCollection._type,
        itemCollection.stacVersion,
        itemCollection.stacExtensions,
        itemCollection.features,
        itemCollection.links
      )
  )

  implicit val decItemCollection: Decoder[ItemCollection] = Decoder.forProduct5(
    "type",
    "stac_version",
    "stac_extensions",
    "features",
    "links"
  )(ItemCollection.apply _)
}
