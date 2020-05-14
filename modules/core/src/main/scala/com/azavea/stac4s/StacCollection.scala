package com.azavea.stac4s

import cats.Eq
import cats.implicits._
import geotrellis.vector.{io => _}
import io.circe._
import io.circe.syntax._

final case class StacCollection(
    stacVersion: String,
    stacExtensions: List[String],
    id: String,
    title: Option[String],
    description: String,
    keywords: List[String],
    license: StacLicense,
    providers: List[StacProvider],
    extent: StacExtent,
    summaries: JsonObject,
    properties: JsonObject,
    links: List[StacLink],
    extensionFields: JsonObject
)

object StacCollection {

  val collectionFields = Set(
    "stac_version",
    "stac_extensions",
    "id",
    "title",
    "description",
    "keywords",
    "license",
    "providers",
    "extent",
    "summaries",
    "properties",
    "links"
  )

  implicit val eqStacCollection: Eq[StacCollection] = Eq.fromUniversalEquals

  implicit val encoderStacCollection: Encoder[StacCollection] = new Encoder[StacCollection] {

    def apply(collection: StacCollection): Json = {
      val baseEncoder: Encoder[StacCollection] = Encoder.forProduct12(
        "stac_version",
        "stac_extensions",
        "id",
        "title",
        "description",
        "keywords",
        "license",
        "providers",
        "extent",
        "summaries",
        "properties",
        "links"
      )(collection =>
        (
          collection.stacVersion,
          collection.stacExtensions,
          collection.id,
          collection.title,
          collection.description,
          collection.keywords,
          collection.license,
          collection.providers,
          collection.extent,
          collection.summaries,
          collection.properties,
          collection.links
        )
      )

      baseEncoder(collection).deepMerge(collection.extensionFields.asJson)
    }
  }

  implicit val decoderStacCollection: Decoder[StacCollection] = { c: HCursor =>
    (
      c.get[String]("stac_version"),
      c.get[Option[List[String]]]("stac_extensions"),
      c.get[String]("id"),
      c.get[Option[String]]("title"),
      c.get[String]("description"),
      c.get[Option[List[String]]]("keywords"),
      c.get[StacLicense]("license"),
      c.get[Option[List[StacProvider]]]("providers"),
      c.get[StacExtent]("extent"),
      c.get[Option[JsonObject]]("summaries"),
      c.get[JsonObject]("properties"),
      c.get[List[StacLink]]("links"),
      c.value.as[JsonObject]
    ).mapN(
      (
          stacVersion: String,
          stacExtensions: Option[List[String]],
          id: String,
          title: Option[String],
          description: String,
          keywords: Option[List[String]],
          license: StacLicense,
          providers: Option[List[StacProvider]],
          extent: StacExtent,
          summaries: Option[JsonObject],
          properties: JsonObject,
          links: List[StacLink],
          extensionFields: JsonObject
      ) =>
        StacCollection(
          stacVersion,
          stacExtensions getOrElse Nil,
          id,
          title,
          description,
          keywords getOrElse List.empty,
          license,
          providers getOrElse List.empty,
          extent,
          summaries getOrElse JsonObject.fromMap(Map.empty),
          properties,
          links,
          extensionFields
        )
    )
  }
}
