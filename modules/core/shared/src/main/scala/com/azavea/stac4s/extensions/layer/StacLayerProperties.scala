package com.azavea.stac4s.extensions.layer

import cats.kernel.Eq
import io.circe.{Decoder, Encoder}

import java.time.Instant

final case class StacLayerProperties(
    startDatetime: Instant,
    endDatetime: Instant
)

object StacLayerProperties {

  implicit val eqLayerProperties: Eq[StacLayerProperties] = Eq.fromUniversalEquals

  implicit val decLayerProperties: Decoder[StacLayerProperties] = Decoder.forProduct2(
    "start_datetime",
    "end_datetime"
  )(StacLayerProperties.apply)

  implicit val encLayerProperties: Encoder[StacLayerProperties] = Encoder.forProduct2(
    "start_datetime",
    "end_datetime"
  )(props => (props.startDatetime, props.endDatetime))
}
