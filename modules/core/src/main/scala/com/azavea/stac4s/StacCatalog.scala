package com.azavea.stac4s

import cats.Eq
import cats.syntax.apply._
import io.circe._
import io.circe.syntax._

final case class StacCatalog(
    stacVersion: String,
    stacExtensions: List[String],
    id: String,
    title: Option[String],
    description: String,
    links: List[StacLink],
    extensionFields: JsonObject = ().asJsonObject
)

object StacCatalog {

  val catalogFields = productFieldNames[StacCatalog]

  implicit val eqStacCatalog: Eq[StacCatalog] = Eq.fromUniversalEquals

  implicit val encCatalog: Encoder[StacCatalog] = { catalog =>
    val baseEncoder: Encoder[StacCatalog] =
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

    baseEncoder(catalog).deepMerge(catalog.extensionFields.asJson)
  }

  implicit val decCatalog: Decoder[StacCatalog] = { c: HCursor =>
    (
      c.get[String]("stac_version"),
      c.get[Option[List[String]]]("stac_extensions"),
      c.get[String]("id"),
      c.get[Option[String]]("title"),
      c.get[String]("description"),
      c.get[List[StacLink]]("links"),
      c.value.as[JsonObject]
    ).mapN(
      (
          version: String,
          extensions: Option[List[String]],
          id: String,
          title: Option[String],
          description: String,
          links: List[StacLink],
          document: JsonObject
      ) =>
        StacCatalog.apply(
          version,
          extensions getOrElse Nil,
          id,
          title,
          description,
          links,
          document.filter({ case (k, _) =>
            !catalogFields.contains(k)
          })
        )
    )
  }
}
