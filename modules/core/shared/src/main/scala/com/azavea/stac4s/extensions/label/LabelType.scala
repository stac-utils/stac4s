package com.azavea.stac4s.extensions.label

import cats.Eq
import io.circe.{Decoder, Encoder}

sealed abstract class LabelType(val repr: String) {
  override def toString: String = repr
}

object LabelType {
  case object Vector extends LabelType("vector")
  case object Raster extends LabelType("raster")

  def fromStringE(s: String): Either[String, LabelType] = s.toLowerCase match {
    case "vector" => Right(Vector)
    case "raster" => Right(Raster)
    case str      => Left(s"$str is not a valid label type. Should be raster or vector")
  }

  // There's no invariant functor with strings here because only two strings map to
  // label types
  implicit val eqLabelType: Eq[LabelType]       = Eq.fromUniversalEquals
  implicit val encLabelType: Encoder[LabelType] = Encoder[String].contramap(_.repr)
  implicit val decLabelType: Decoder[LabelType] = Decoder[String].emap(fromStringE _)
}
