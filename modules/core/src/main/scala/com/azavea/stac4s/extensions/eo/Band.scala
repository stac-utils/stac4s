package com.azavea.stac4s.extensions.eo

import cats.Eq
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.types.numeric.PosInt
import io.circe._
import io.circe.refined._

case class Band(
    name: NonEmptyString,
    commonName: Option[NonEmptyString],
    description: Option[NonEmptyString],
    centerWavelength: Option[PosInt],
    fullWidthHalfMax: Option[PosInt]
)

object Band {

  implicit val eqBand: Eq[Band] = Eq.fromUniversalEquals

  implicit val encBand: Encoder[Band] = Encoder.forProduct5(
    "name",
    "common_name",
    "description",
    "center_wavelength",
    "full_width_half_max"
  )(band => (band.name, band.commonName, band.description, band.centerWavelength, band.fullWidthHalfMax))

  implicit val decBand: Decoder[Band] = Decoder.forProduct5(
    "name",
    "common_name",
    "description",
    "center_wavelength",
    "full_width_half_max"
  )(Band.apply)
}
