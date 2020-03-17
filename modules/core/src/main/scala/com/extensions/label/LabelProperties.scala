package com.azavea.stac4s.extensions.label

import io.circe.{Decoder, Encoder}

sealed abstract class LabelProperties(val repr: String) {
  override def toString: String = repr
}

object LabelProperties {
  case class VectorLabelProperties(fields: List[String]) extends LabelProperties("vector")
  case object RasterLabelProperties                      extends LabelProperties("raster")

  implicit val decLabelProperties: Decoder[LabelProperties] = Decoder[Option[List[String]]] map {
    case None         => RasterLabelProperties
    case Some(fields) => VectorLabelProperties(fields)
  }

  implicit val encLabelProperties: Encoder[LabelProperties] = Encoder[Option[List[String]]].contramap({
    case RasterLabelProperties         => None
    case VectorLabelProperties(fields) => Some(fields)
  })
}
