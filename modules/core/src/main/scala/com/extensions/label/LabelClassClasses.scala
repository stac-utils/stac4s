package com.azavea.stac4s.extensions.label

import cats.implicits._
import io.circe._
import io.circe.syntax._

sealed abstract class LabelClassClasses

object LabelClassClasses {
  case class NamedLabelClasses(names: List[String])   extends LabelClassClasses
  case class NumberedLabelClasses(indices: List[Int]) extends LabelClassClasses

  implicit val encNamedLabelClasses: Encoder[NamedLabelClasses] =
    Encoder[List[String]].contramap(_.names)

  implicit val decNamedLabelClasses: Decoder[NamedLabelClasses] =
    Decoder[List[String]].map(NamedLabelClasses.apply _)

  implicit val encNumberedLabelClasses: Encoder[NumberedLabelClasses] =
    Encoder[List[Int]].contramap(_.indices)

  implicit val decNumberedLabelClasses: Decoder[NumberedLabelClasses] =
    Decoder[List[Int]].map(NumberedLabelClasses.apply _)

  implicit val encLabelClassClasses: Encoder[LabelClassClasses] = new Encoder[LabelClassClasses] {

    def apply(t: LabelClassClasses): Json = t match {
      case named: NamedLabelClasses       => named.asJson
      case numbered: NumberedLabelClasses => numbered.asJson
    }
  }

  implicit val decLabelClassClassels: Decoder[LabelClassClasses] = Decoder[NumberedLabelClasses].widen or
    Decoder[NamedLabelClasses].widen
}
