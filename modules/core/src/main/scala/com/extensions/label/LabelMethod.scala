package com.azavea.stac4s.extensions.label

import cats.Eq
import cats.implicits._
import io.circe.{Decoder, Encoder}

sealed abstract class LabelMethod(val repr: String) {
  override def toString: String = repr
}

object LabelMethod {
  case object Manual                          extends LabelMethod("manual")
  case object Automatic                       extends LabelMethod("automatic")
  case class VendorMethod(methodName: String) extends LabelMethod(methodName)

  implicit def eqLabelMethod: Eq[LabelMethod] = Eq[String].imap(fromString _)(_.repr)

  implicit val decLabelMethod: Decoder[LabelMethod] = Decoder[String].map(fromString _)
  implicit val encLabelMethod: Encoder[LabelMethod] = Encoder[String].contramap(_.repr)

  def fromString(s: String): LabelMethod = s.toLowerCase match {
    case "manual"    => Manual
    case "automatic" => Automatic
    case s           => VendorMethod(s)
  }
}
