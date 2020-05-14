package com.azavea.stac4s

import cats.Eq
import cats.implicits._
import io.circe._
import io.circe.syntax._

final case class StacItemAsset(
    href: String,
    title: Option[String],
    description: Option[String],
    roles: List[StacAssetRole],
    _type: Option[StacMediaType],
    extensionFields: JsonObject
)

object StacItemAsset {

  implicit val eqStacItemAsset: Eq[StacItemAsset] = Eq.fromUniversalEquals

  implicit val encStacItemAsset: Encoder[StacItemAsset] = new Encoder[StacItemAsset] {

    def apply(asset: StacItemAsset): Json = {

      val baseEncoder: Encoder[StacItemAsset] =
        Encoder.forProduct5("href", "title", "description", "roles", "type")(asset =>
          (asset.href, asset.title, asset.description, asset.roles, asset._type)
        )
      baseEncoder(asset).deepMerge(asset.extensionFields.asJson)
    }
  }

  implicit val decStacItemAsset: Decoder[StacItemAsset] = { c: HCursor =>
    (
      c.get[String]("href"),
      c.get[Option[String]]("title"),
      c.get[Option[String]]("description"),
      c.get[List[StacAssetRole]]("roles"),
      c.get[Option[StacMediaType]]("type"),
      c.value.as[JsonObject]
    ).mapN(
      (
          href: String,
          title: Option[String],
          description: Option[String],
          roles: List[StacAssetRole],
          mediaType: Option[StacMediaType],
          document: JsonObject
      ) =>
        StacItemAsset(href, title, description, roles, mediaType, document.filter({
          case (k, _) => !Set("href", "title", "description", "roles", "type").contains(k)
        }))
    )
  }
}
