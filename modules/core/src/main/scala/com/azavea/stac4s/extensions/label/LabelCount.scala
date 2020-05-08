package com.azavea.stac4s.extensions.label

import cats.Eq
import io.circe.{Decoder, Encoder}

case class LabelCount(
    name: String,
    count: Int
)

object LabelCount {

  implicit val eqLabelCount: Eq[LabelCount] = Eq.fromUniversalEquals

  implicit val decLabelCount: Decoder[LabelCount] = Decoder.forProduct2(
    "name",
    "count"
  )(LabelCount.apply)

  implicit val encLabelCount: Encoder[LabelCount] = Encoder.forProduct2(
    "name",
    "count"
  )(labelCount => (labelCount.name, labelCount.count))
}
