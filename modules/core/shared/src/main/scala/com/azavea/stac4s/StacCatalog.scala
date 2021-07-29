package com.azavea.stac4s

import com.azavea.stac4s.types.CatalogType

import cats.Eq
import cats.syntax.apply._
import cats.syntax.either._
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
      Encoder.forProduct7(
        "type",
        "stac_version",
        "stac_extensions",
        "id",
        "title",
        "description",
        "links"
      )(catalog =>
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

    baseEncoder(catalog).deepMerge(catalog.extensionFields.asJson).dropNullValues
  }

  implicit val decCatalog: Decoder[StacCatalog] = new Decoder[StacCatalog] {

    override def decodeAccumulating(c: HCursor) = {
      (
        c.get[CatalogType]("type").toValidatedNel,
        c.get[String]("stac_version").toValidatedNel,
        c.get[Option[List[String]]]("stac_extensions").toValidatedNel,
        c.get[String]("id").toValidatedNel,
        c.get[Option[String]]("title").toValidatedNel,
        c.get[String]("description").toValidatedNel,
        c.get[List[StacLink]]("links").toValidatedNel,
        c.value.as[JsonObject].toValidatedNel
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

    def apply(c: HCursor) = decodeAccumulating(c).toEither.leftMap(_.head)
  }
}
