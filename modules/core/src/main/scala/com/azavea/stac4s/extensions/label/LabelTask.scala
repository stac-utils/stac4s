package com.azavea.stac4s.extensions.label

import cats.Eq
import cats.implicits._
import io.circe.{Decoder, Encoder}

sealed abstract class LabelTask(val repr: String) {
  override def toString = repr
}

object LabelTask {

  case object Regression                  extends LabelTask("regression")
  case object Segmentation                extends LabelTask("segmentation")
  case object Detection                   extends LabelTask("detection")
  case object Classification              extends LabelTask("classification")
  case class VendorTask(taskName: String) extends LabelTask("vendor")

  implicit def eqLabelTask: Eq[LabelTask] = Eq[String].imap(fromString _)({
    case VendorTask(s) => s
    case fixedTask => fixedTask.repr
  })

  implicit val decLabelTask: Decoder[LabelTask] = Decoder[String].map(fromString _)
  implicit val encLabelTask: Encoder[LabelTask] = Encoder[String].contramap(_.repr)

  def fromString(s: String): LabelTask = s.toLowerCase match {
    case "regression"     => Regression
    case "segmentation"   => Segmentation
    case "detection"      => Detection
    case "classification" => Classification
    case s                => VendorTask(s)
  }
}
