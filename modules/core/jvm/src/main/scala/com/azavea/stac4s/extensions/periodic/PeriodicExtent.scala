package com.azavea.stac4s.extensions.periodic

import com.azavea.stac4s.meta._

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.threeten.extra.PeriodDuration
import io.circe.Decoder
import io.circe.Encoder
import cats.kernel.Eq
import com.azavea.stac4s.extensions.IntervalExtension

final case class PeriodicExtent(
    period: PeriodDuration
)

object PeriodicExtent {
  implicit val decPeriodicExtent: Decoder[PeriodicExtent]                = deriveDecoder
  implicit val encPeriodicExtentObject: Encoder.AsObject[PeriodicExtent] = deriveEncoder
  implicit val eqPeriodicExtent: Eq[PeriodicExtent]                      = Eq.fromUniversalEquals

  implicit val intervalExtension: IntervalExtension[PeriodicExtent] = IntervalExtension.instance[PeriodicExtent]
}
