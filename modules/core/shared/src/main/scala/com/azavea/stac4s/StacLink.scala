package com.azavea.stac4s

import cats.syntax.apply._
import io.circe._
import io.circe.syntax._

final case class StacLink(
    href: String,
    rel: StacLinkType,
    _type: Option[StacMediaType],
    title: Option[String],
    extensionFields: JsonObject = ().asJsonObject
)

object StacLink {
  val linkFields = productFieldNames[StacLink]

  implicit val encStacLink: Encoder[StacLink] = { link =>
    val baseEncoder = Encoder
      .forProduct4(
        "href",
        "rel",
        "type",
        "title"
      )((link: StacLink) => (link.href, link.rel, link._type, link.title))

    baseEncoder(link).deepMerge(link.extensionFields.asJson)
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
        StacLink(
          href,
          rel,
          _type,
          title,
          document.filter({ case (k, _) =>
            !linkFields.contains(k)
          })
        )
    )
  }
}
