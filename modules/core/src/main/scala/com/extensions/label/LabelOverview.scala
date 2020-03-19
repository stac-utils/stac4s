package com.azavea.stac4s.extensions.label

import cats.Eq
import cats.implicits._
import io.circe.{Decoder, Encoder, HCursor}

case class LabelOverview(
    propertyKey: String,
    counts: List[LabelCount],
    statistics: List[LabelStats]
)

object LabelOverview {

  implicit val eqLabelOverview: Eq[LabelOverview] = Eq.fromUniversalEquals

  implicit val decLabelOverview: Decoder[LabelOverview] = new Decoder[LabelOverview] {

    def apply(c: HCursor) =
      (
        c.downField("property_key").as[String],
        c.downField("counts").as[Option[List[LabelCount]]],
        c.downField("statistics").as[Option[List[LabelStats]]]
      ).mapN((key: String, counts: Option[List[LabelCount]], statistics: Option[List[LabelStats]]) =>
        LabelOverview(
          key,
          counts getOrElse Nil,
          statistics getOrElse Nil
        )
      )
  }

  implicit val encLabelOverview: Encoder[LabelOverview] = Encoder.forProduct3(
    "property_key",
    "counts",
    "statistics"
  )(labelOverview => (labelOverview.propertyKey, labelOverview.counts, labelOverview.statistics))
}
