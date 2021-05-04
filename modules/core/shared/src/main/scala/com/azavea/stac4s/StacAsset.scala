package com.azavea.stac4s

import cats.Eq
import cats.syntax.apply._
import io.circe._
import io.circe.syntax._

final case class StacAsset(
    href: String,
    title: Option[String],
    description: Option[String],
    roles: Set[StacAssetRole],
    _type: Option[StacMediaType],
    extensionFields: JsonObject = ().asJsonObject
)

object StacAsset {
  val assetFields = productFieldNames[StacAsset]

  implicit val eqStacAsset: Eq[StacAsset] = Eq.fromUniversalEquals

  implicit val encStacAsset: Encoder[StacAsset] = { asset =>
    val baseEncoder: Encoder[StacAsset] =
      Encoder.forProduct5("href", "title", "description", "roles", "type")(asset =>
        (asset.href, asset.title, asset.description, asset.roles, asset._type)
      )
    baseEncoder(asset).deepMerge(asset.extensionFields.asJson).dropNullValues
  }

  implicit val decStacAsset: Decoder[StacAsset] = { c: HCursor =>
    (
      c.get[String]("href"),
      c.get[Option[String]]("title"),
      c.get[Option[String]]("description"),
      c.get[Option[Set[StacAssetRole]]]("roles"),
      c.get[Option[StacMediaType]]("type"),
      c.value.as[JsonObject]
    ).mapN(
      (
          href: String,
          title: Option[String],
          description: Option[String],
          roles: Option[Set[StacAssetRole]],
          mediaType: Option[StacMediaType],
          document: JsonObject
      ) =>
        StacAsset(
          href,
          title,
          description,
          roles getOrElse Set.empty,
          mediaType,
          document.filter({ case (k, _) =>
            !assetFields.contains(k)
          })
        )
    )
  }
}
