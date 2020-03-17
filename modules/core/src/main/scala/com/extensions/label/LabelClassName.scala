package com.azavea.stac4s.extensions.label

import cats.Eq
import cats.implicits._
import io.circe.{Decoder, Encoder}

sealed abstract class LabelClassName(repr: String) {
  override def toString: String = repr
}

object LabelClassName {
  case object Raster               extends LabelClassName("raster")
  case class VectorName(s: String) extends LabelClassName(s)

  def fromOption(o: Option[String]): LabelClassName = o match {
    case Some(s) => VectorName(s)
    case None    => Raster
  }

  def toOption(name: LabelClassName) = name match {
    case Raster        => None
    case VectorName(s) => Some(s)
  }

  implicit val eqLabelClassName: Eq[LabelClassName] = Eq[Option[String]].imap(fromOption)(toOption)

  implicit val encLabelClassName: Encoder[LabelClassName] = Encoder[Option[String]].contramap(toOption)

  implicit val decLabelClassName: Decoder[LabelClassName] = Decoder[Option[String]].map(fromOption)
}
