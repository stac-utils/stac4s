package com.azavea.stac4s.extensions.label

import io.circe.{Decoder, Encoder}

case class LabelClass(
    name: LabelClassName,
    classes: LabelClassClasses
)

object LabelClass {

  implicit val decLabelClass: Decoder[LabelClass] = Decoder.forProduct2(
    "name",
    "classes"
  )(LabelClass.apply _)

  implicit val encLabelClass: Encoder[LabelClass] = Encoder.forProduct2(
    "name",
    "classes"
  )(labelClass => (labelClass.name, labelClass.classes))
}
