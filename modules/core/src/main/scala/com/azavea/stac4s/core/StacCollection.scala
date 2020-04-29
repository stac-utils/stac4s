package com.azavea.stac4s.core

import cats.Eq
import geotrellis.vector.{io => _}
import io.circe._

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
    properties: JsonObject,
    links: List[StacLink]
)

object StacCollection {

  implicit val eqStacCollection: Eq[StacCollection] = Eq.fromUniversalEquals

  implicit val encoderStacCollection: Encoder[StacCollection] =
    Encoder.forProduct11(
      "stac_version",
      "stac_extensions",
      "id",
      "title",
      "description",
      "keywords",
      "license",
      "providers",
      "extent",
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
        collection.properties,
        collection.links
      )
    )

  implicit val decoderStacCollection: Decoder[StacCollection] =
    Decoder.forProduct11(
      "stac_version",
      "stac_extensions",
      "id",
      "title",
      "description",
      "keywords",
      "license",
      "providers",
      "extent",
      "properties",
      "links"
    )(
      (
          stacVersion: String,
          stacExtensions: List[String],
          id: String,
          title: Option[String],
          description: String,
          keywords: Option[List[String]],
          license: StacLicense,
          providers: Option[List[StacProvider]],
          extent: StacExtent,
          properties: Option[JsonObject],
          links: List[StacLink]
      ) =>
        StacCollection(
          stacVersion,
          stacExtensions,
          id,
          title,
          description,
          keywords getOrElse List.empty,
          license,
          providers getOrElse List.empty,
          extent,
          properties getOrElse JsonObject.fromMap(Map.empty),
          links
        )
    )
}
