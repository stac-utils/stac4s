package com.azavea.stac4s.extensions.periodic

import com.azavea.stac4s.extensions.IntervalExtension
import com.azavea.stac4s.meta._

import cats.kernel.Eq
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.threeten.extra.PeriodDuration

final case class PeriodicExtent(
    period: PeriodDuration
)

object PeriodicExtent {
  implicit val decPeriodicExtent: Decoder[PeriodicExtent]                = deriveDecoder
  implicit val encPeriodicExtentObject: Encoder.AsObject[PeriodicExtent] = deriveEncoder
  implicit val eqPeriodicExtent: Eq[PeriodicExtent]                      = Eq.fromUniversalEquals

  implicit val intervalExtension: IntervalExtension[PeriodicExtent] = IntervalExtension.instance[PeriodicExtent]
}
