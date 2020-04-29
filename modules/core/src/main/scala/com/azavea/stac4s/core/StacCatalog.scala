package com.azavea.stac4s.core

import cats.Eq
import io.circe._

final case class StacCatalog(
    stacVersion: String,
    stacExtensions: List[String],
    id: String,
    title: Option[String],
    description: String,
    links: List[StacLink]
)

object StacCatalog {

  implicit val eqStacCatalog: Eq[StacCatalog] = Eq.fromUniversalEquals

  implicit val encCatalog: Encoder[StacCatalog] =
    Encoder.forProduct6("stac_version", "stac_extensions", "id", "title", "description", "links")(catalog =>
      (
        catalog.stacVersion,
        catalog.stacExtensions,
        catalog.id,
        catalog.title,
        catalog.description,
        catalog.links
      )
    )

  implicit val decCatalog: Decoder[StacCatalog] =
    Decoder.forProduct6("stac_version", "stac_extensions", "id", "title", "description", "links")(StacCatalog.apply _)
}
