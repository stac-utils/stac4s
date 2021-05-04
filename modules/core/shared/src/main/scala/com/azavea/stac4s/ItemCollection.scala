package com.azavea.stac4s

import cats.Eq
import cats.syntax.apply._
import io.circe._
import io.circe.refined._
import io.circe.syntax._

final case class ItemCollection(
    _type: String = "FeatureCollection",
    stacVersion: StacVersion,
    stacExtensions: List[String],
    features: List[StacItem],
    links: List[StacLink],
    extensionFields: JsonObject = ().asJsonObject
)

object ItemCollection {

  val itemCollectionFields = productFieldNames[ItemCollection]

  implicit val eqItemCollection: Eq[ItemCollection] = Eq.fromUniversalEquals

  implicit val encItemCollection: Encoder[ItemCollection] = new Encoder[ItemCollection] {

    def apply(collection: ItemCollection): Json = {
      val baseEncoder: Encoder[ItemCollection] = Encoder.forProduct5(
        "type",
        "stac_version",
        "stac_extensions",
        "features",
        "links"
      )(itemCollection =>
        (
          itemCollection._type,
          itemCollection.stacVersion,
          itemCollection.stacExtensions,
          itemCollection.features,
          itemCollection.links
        )
      )

      baseEncoder(collection).deepMerge(collection.extensionFields.asJson).dropNullValues
    }
  }

  implicit val decItemCollection: Decoder[ItemCollection] = { c: HCursor =>
    (
      c.get[String]("type"),
      c.get[StacVersion]("stac_version"),
      c.get[Option[List[String]]]("stac_extensions"),
      c.get[List[StacItem]]("features"),
      c.get[Option[List[StacLink]]]("links"),
      c.value.as[JsonObject]
    ).mapN(
      (
          _type: String,
          stacVersion: StacVersion,
          extensions: Option[List[String]],
          features: List[StacItem],
          links: Option[List[StacLink]],
          document: JsonObject
      ) =>
        ItemCollection(
          _type,
          stacVersion,
          extensions getOrElse Nil,
          features,
          links getOrElse Nil,
          document.filter({ case (k, _) =>
            !itemCollectionFields.contains(k)
          })
        )
    )
  }
}
