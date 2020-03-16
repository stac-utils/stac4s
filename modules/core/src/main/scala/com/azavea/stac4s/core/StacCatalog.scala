package com.azavea.stac4s

import cats.Eq
import io.circe._

final case class StacCatalog(
    stacVersion: String,
    id: String,
    title: Option[String],
    description: String,
    links: List[StacLink]
)

object StacCatalog {

  implicit val eqStacCatalog: Eq[StacCatalog] = Eq.fromUniversalEquals

  implicit val encCatalog: Encoder[StacCatalog] =
    Encoder.forProduct5("stac_version", "id", "title", "description", "links")(catalog =>
      (
        catalog.stacVersion,
        catalog.id,
        catalog.title,
        catalog.description,
        catalog.links
      )
    )

  implicit val decCatalog: Decoder[StacCatalog] =
    Decoder.forProduct5("stac_version", "id", "title", "description", "links")(StacCatalog.apply _)
}
