package com.azavea.stac4s

import cats.data.NonEmptyList
import cats.kernel.Eq
import cats.syntax.apply._
import eu.timepit.refined.types.string
import io.circe.refined._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor, JsonObject}

import java.time.Instant

case class ItemProperties(
    datetime: ItemDatetime,
    title: Option[string.NonEmptyString],
    description: Option[string.NonEmptyString],
    created: Option[Instant],
    updated: Option[Instant],
    license: Option[StacLicense],
    providers: Option[NonEmptyList[StacProvider]],
    platform: Option[string.NonEmptyString],
    instruments: Option[NonEmptyList[string.NonEmptyString]],
    constellation: Option[string.NonEmptyString],
    mission: Option[string.NonEmptyString],
    gsd: Option[Double],
    extensionFields: JsonObject
)

object ItemProperties {

  implicit val eqItemProperties: Eq[ItemProperties] = Eq.fromUniversalEquals

  val itemPropertiesFields = productFieldNames[ItemProperties] ++ List("datetime", "start_datetime", "end_datetime")

  implicit val encItemProperties: Encoder[ItemProperties] = { properties =>
    val baseEncoder: Encoder[ItemProperties] = Encoder
      .forProduct11(
        "title",
        "description",
        "created",
        "updated",
        "license",
        "providers",
        "platform",
        "instruments",
        "constellation",
        "mission",
        "gsd"
      )((props: ItemProperties) =>
        (
          props.title,
          props.description,
          props.created,
          props.updated,
          props.license,
          props.providers,
          props.platform,
          props.instruments,
          props.constellation,
          props.mission,
          props.gsd
        )
      )
    baseEncoder(properties).dropNullValues
      .deepMerge(properties.datetime.asJson)
      .deepMerge(properties.extensionFields.asJson)
  }

  implicit val decItemProperties: Decoder[ItemProperties] = { cursor: HCursor =>
    (
      cursor.as[ItemDatetime],
      cursor.get[Option[string.NonEmptyString]]("title"),
      cursor.get[Option[string.NonEmptyString]]("description"),
      cursor.get[Option[Instant]]("created"),
      cursor.get[Option[Instant]]("updated"),
      cursor.get[Option[StacLicense]]("license"),
      cursor.get[Option[NonEmptyList[StacProvider]]]("providers"),
      cursor.get[Option[string.NonEmptyString]]("platform"),
      cursor.get[Option[NonEmptyList[string.NonEmptyString]]]("instruments"),
      cursor.get[Option[string.NonEmptyString]]("constellation"),
      cursor.get[Option[string.NonEmptyString]]("mission"),
      cursor.get[Option[Double]]("gsd"),
      cursor.value.as[JsonObject]
    ) mapN {
      case (
            dt,
            title,
            description,
            created,
            updated,
            license,
            providers,
            platform,
            instruments,
            constellation,
            mission,
            gsd,
            fields
          ) =>
        ItemProperties(
          dt,
          title,
          description,
          created,
          updated,
          license,
          providers,
          platform,
          instruments,
          constellation,
          mission,
          gsd,
          fields.filter({ case (k, _) =>
            !itemPropertiesFields.contains(k)
          })
        )
    }
  }
}
