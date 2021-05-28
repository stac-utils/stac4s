package com.azavea.stac4s

import com.azavea.stac4s.types.CollectionType

import cats.Eq
import cats.syntax.apply._
import io.circe._
import io.circe.refined._
import io.circe.syntax._
import eu.timepit.refined.types.string

final case class StacCollection(
    _type: CollectionType,
    stacVersion: String,
    stacExtensions: List[String],
    id: String,
    title: Option[String],
    description: String,
    keywords: List[String],
    license: StacLicense,
    providers: List[StacProvider],
    extent: StacExtent,
    summaries: Map[string.NonEmptyString, SummaryValue],
    properties: JsonObject,
    links: List[StacLink],
    assets: Option[Map[String, StacAsset]],
    extensionFields: JsonObject = ().asJsonObject
)

object StacCollection {
  val collectionFields = productFieldNames[StacCollection]

  implicit val eqStacCollection: Eq[StacCollection] = Eq.fromUniversalEquals

  implicit val encoderStacCollection: Encoder[StacCollection] = { collection =>
    val baseEncoder: Encoder[StacCollection] = Encoder.forProduct14(
      "type",
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
      "links",
      "assets"
    )(collection =>
      (
        collection._type,
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
        collection.links,
        collection.assets
      )
    )

    baseEncoder(collection).deepMerge(collection.extensionFields.asJson).dropNullValues
  }

  implicit val decoderStacCollection: Decoder[StacCollection] = { c: HCursor =>
    (
      c.get[CollectionType]("type"),
      c.get[String]("stac_version"),
      c.get[Option[List[String]]]("stac_extensions"),
      c.get[String]("id"),
      c.get[Option[String]]("title"),
      c.get[String]("description"),
      c.get[Option[List[String]]]("keywords"),
      c.get[StacLicense]("license"),
      c.get[Option[List[StacProvider]]]("providers"),
      c.get[StacExtent]("extent"),
      c.get[Option[Map[string.NonEmptyString, SummaryValue]]]("summaries"),
      c.get[Option[JsonObject]]("properties"),
      c.get[List[StacLink]]("links"),
      c.get[Option[Map[String, StacAsset]]]("assets"),
      c.value.as[JsonObject]
    ).mapN(
      (
          _type: CollectionType,
          stacVersion: String,
          stacExtensions: Option[List[String]],
          id: String,
          title: Option[String],
          description: String,
          keywords: Option[List[String]],
          license: StacLicense,
          providers: Option[List[StacProvider]],
          extent: StacExtent,
          summaries: Option[Map[string.NonEmptyString, SummaryValue]],
          properties: Option[JsonObject],
          links: List[StacLink],
          assets: Option[Map[String, StacAsset]],
          extensionFields: JsonObject
      ) =>
        StacCollection(
          _type,
          stacVersion,
          stacExtensions getOrElse Nil,
          id,
          title,
          description,
          keywords getOrElse List.empty,
          license,
          providers getOrElse List.empty,
          extent,
          summaries getOrElse Map.empty,
          properties getOrElse JsonObject.fromMap(Map.empty),
          links,
          assets,
          extensionFields.filter({ case (k, _) =>
            !collectionFields.contains(k)
          })
        )
    )
  }
}
