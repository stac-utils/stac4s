package com.azavea.stac4s

import cats.Eq
import cats.implicits._
import io.circe._
import io.circe.syntax._
import shapeless.LabelledGeneric
import shapeless.ops.record.Keys

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

  private val generic = LabelledGeneric[StacCatalog]
  private val keys    = Keys[generic.Repr].apply
  val catalogFields   = keys.toList.flatMap(field => substituteFieldName(field.name)).toSet

  implicit val eqStacCatalog: Eq[StacCatalog] = Eq.fromUniversalEquals

  implicit val encCatalog: Encoder[StacCatalog] = new Encoder[StacCatalog] {

    def apply(catalog: StacCatalog): Json = {
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
  }

  implicit val decCatalog: Decoder[StacCatalog] = { c: HCursor =>
    (
      c.get[String]("stac_version"),
      c.get[List[String]]("stac_extensions"),
      c.get[String]("id"),
      c.get[Option[String]]("title"),
      c.get[String]("description"),
      c.get[List[StacLink]]("links"),
      c.value.as[JsonObject]
    ).mapN(
      (
          version: String,
          extensions: List[String],
          id: String,
          title: Option[String],
          description: String,
          links: List[StacLink],
          document: JsonObject
      ) =>
        StacCatalog.apply(
          version,
          extensions,
          id,
          title,
          description,
          links,
          document.filter({
            case (k, _) => !catalogFields.contains(k)
          })
        )
    )
  }
}
