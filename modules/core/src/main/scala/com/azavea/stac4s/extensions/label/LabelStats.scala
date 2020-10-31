package com.azavea.stac4s.extensions.label

import cats.Eq
import io.circe.{Decoder, Encoder}

case class LabelStats(
    name: String,
    value: Double
)

object LabelStats {

  implicit val eqLabelStats: Eq[LabelStats] = Eq.fromUniversalEquals

  implicit val decLabelStats: Decoder[LabelStats] = Decoder.forProduct2("name", "value")(
    LabelStats.apply
  )

  implicit val encLabelStats: Encoder[LabelStats] =
    Encoder.forProduct2("name", "value")(labelStats => (labelStats.name, labelStats.value))
}
