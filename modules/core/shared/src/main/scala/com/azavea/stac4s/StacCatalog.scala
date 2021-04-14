package com.azavea.stac4s

import com.azavea.stac4s.types.CatalogType

import cats.Eq
import cats.syntax.apply._
import io.circe._
import io.circe.refined._
import io.circe.syntax._

final case class StacCatalog(
    _type: CatalogType,
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
      Encoder.forProduct7("type", "stac_version", "stac_extensions", "id", "title", "description", "links")(catalog =>
        (
          catalog._type,
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
      c.get[CatalogType]("type"),
      c.get[String]("stac_version"),
      c.get[Option[List[String]]]("stac_extensions"),
      c.get[String]("id"),
      c.get[Option[String]]("title"),
      c.get[String]("description"),
      c.get[List[StacLink]]("links"),
      c.value.as[JsonObject]
    ).mapN(
      (
          catalogType: CatalogType,
          version: String,
          extensions: Option[List[String]],
          id: String,
          title: Option[String],
          description: String,
          links: List[StacLink],
          document: JsonObject
      ) =>
        StacCatalog.apply(
          catalogType,
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
