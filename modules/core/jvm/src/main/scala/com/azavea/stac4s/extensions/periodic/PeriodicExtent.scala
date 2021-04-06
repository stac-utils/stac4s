package com.azavea.stac4s.extensions.periodic

import com.azavea.stac4s.meta._

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.threeten.extra.PeriodDuration
import io.circe.Decoder
import io.circe.Encoder
import cats.kernel.Eq

final case class PeriodicExtent(
    period: PeriodDuration
)

object PeriodicExtent {
  implicit val decPeriodicExtent: Decoder[PeriodicExtent] = deriveDecoder
  implicit val encPeriodicExtent: Encoder[PeriodicExtent] = deriveEncoder
  implicit val eqPeriodicExtent: Eq[PeriodicExtent]       = Eq.fromUniversalEquals
}
