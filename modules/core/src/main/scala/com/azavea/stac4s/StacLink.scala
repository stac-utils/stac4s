package com.azavea.stac4s

import cats.implicits._
import io.circe._
import io.circe.syntax._
import shapeless.LabelledGeneric
import shapeless.ops.record.Keys

final case class StacLink(
    href: String,
    rel: StacLinkType,
    _type: Option[StacMediaType],
    title: Option[String],
    extensionFields: JsonObject = ().asJsonObject
)

object StacLink {

  private val generic = LabelledGeneric[StacLink]
  private val keys    = Keys[generic.Repr].apply
  val linkFields      = keys.toList.flatMap(field => substituteFieldName(field.name)).toSet

  implicit val encStacLink: Encoder[StacLink] = new Encoder[StacLink] {

    def apply(link: StacLink): Json = {
      val baseEncoder = Encoder
        .forProduct4(
          "href",
          "rel",
          "type",
          "title"
        )((link: StacLink) => (link.href, link.rel, link._type, link.title))

      baseEncoder(link).deepMerge(link.extensionFields.asJson)
    }
  }

  implicit val decStacLink: Decoder[StacLink] = { c: HCursor =>
    (
      c.downField("href").as[String],
      c.downField("rel").as[StacLinkType],
      c.get[Option[StacMediaType]]("type"),
      c.get[Option[String]]("title"),
      c.value.as[JsonObject]
    ).mapN(
      (
          href: String,
          rel: StacLinkType,
          _type: Option[StacMediaType],
          title: Option[String],
          document: JsonObject
      ) =>
        StacLink(href, rel, _type, title, document.filter({
          case (k, _) => !linkFields.contains(k)
        }))
    )
  }
}
