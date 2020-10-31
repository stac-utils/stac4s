package com.azavea.stac4s.extensions.label

import cats.Eq
import cats.syntax.invariant._
import io.circe.{Decoder, Encoder}

sealed abstract class LabelProperties(val repr: String) {
  override def toString: String = repr
}

object LabelProperties {
  case class VectorLabelProperties(fields: List[String]) extends LabelProperties("vector")
  case object RasterLabelProperties                      extends LabelProperties("raster")

  def fromOption(o: Option[List[String]]): LabelProperties = o match {
    case Some(fields) => VectorLabelProperties(fields)
    case None         => RasterLabelProperties
  }

  def toOption(props: LabelProperties): Option[List[String]] = props match {
    case VectorLabelProperties(fields) => Some(fields)
    case RasterLabelProperties         => None
  }

  implicit val eqLabelProperties: Eq[LabelProperties] = Eq[Option[List[String]]].imap(fromOption)(toOption)

  implicit val decLabelProperties: Decoder[LabelProperties] = Decoder[Option[List[String]]] map {
    fromOption
  }

  implicit val encLabelProperties: Encoder[LabelProperties] = Encoder[Option[List[String]]].contramap(toOption)
}
