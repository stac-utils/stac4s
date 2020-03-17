package com.azavea.stac4s.extensions.label

import io.circe.{Decoder, Encoder}

sealed abstract class LabelClassName(repr: String) {
  override def toString: String = repr
}

object LabelClassName {
  case object Raster               extends LabelClassName("raster")
  case class VectorName(s: String) extends LabelClassName(s)

  implicit val encLabelClassName: Encoder[LabelClassName] = Encoder[Option[String]].contramap({
    case Raster        => None
    case VectorName(s) => Some(s)
  })

  implicit val decLabelClassName: Decoder[LabelClassName] = Decoder[Option[String]].map({
    case None    => Raster
    case Some(s) => VectorName(s)
  })
}
