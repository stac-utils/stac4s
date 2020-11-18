package com.azavea.stac4s.extensions.label

import com.azavea.stac4s.extensions.LinkExtension

import cats.data.NonEmptyList
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json}

case class LabelLinkExtension(assets: NonEmptyList[String])

object LabelLinkExtension {

  implicit val encLabelLinkExtensionObject: Encoder.AsObject[LabelLinkExtension] = Encoder
    .AsObject[Map[String, Json]]
    .contramapObject((extensionFields: LabelLinkExtension) => Map("label:assets" -> extensionFields.assets.asJson))

  implicit val decLabelLinkExtension: Decoder[LabelLinkExtension] =
    Decoder.forProduct1("label:assets")(LabelLinkExtension.apply)

  implicit val linkExtension: LinkExtension[LabelLinkExtension] = LinkExtension.instance
}
