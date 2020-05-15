package com.azavea.stac4s

import cats.implicits._
import io.circe._
import io.circe.syntax._

final case class StacLink(
    href: String,
    rel: StacLinkType,
    _type: Option[StacMediaType],
    title: Option[String],
    extensionFields: JsonObject
)

object StacLink {

  val linkFields = Set("href", "rel", "type", "title")

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
