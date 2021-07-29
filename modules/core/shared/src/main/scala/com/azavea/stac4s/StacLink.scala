package com.azavea.stac4s

import cats.syntax.apply._
import cats.syntax.either._
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

    baseEncoder(link).deepMerge(link.extensionFields.asJson).dropNullValues
  }

  implicit val decStacLink: Decoder[StacLink] = new Decoder[StacLink] {

    override def decodeAccumulating(c: HCursor) = {
      (
        c.downField("href").as[String].toValidatedNel,
        c.downField("rel").as[StacLinkType].toValidatedNel,
        c.get[Option[StacMediaType]]("type").toValidatedNel,
        c.get[Option[String]]("title").toValidatedNel,
        c.value.as[JsonObject].toValidatedNel
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

    def apply(c: HCursor) = decodeAccumulating(c).toEither.leftMap(_.head)
  }
}
