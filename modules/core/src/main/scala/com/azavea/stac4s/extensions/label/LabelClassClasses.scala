package com.azavea.stac4s.extensions.label

import cats.data.NonEmptyList
import cats.Eq
import io.circe._
import io.circe.syntax._

sealed abstract class LabelClassClasses

object LabelClassClasses {
  // why non-empty? empty lists don't round trip, since we don't know whether
  // they're empty lists of names or numbers. That's bad for tests, and also
  // it doesn't really make any sense to be vectors labeled without classes,
  // so I think the stricter requirement over the STAC spec is fine in this case
  // https://github.com/radiantearth/stac-spec/tree/master/extensions/label#class-object
  case class NamedLabelClasses(names: NonEmptyList[String])   extends LabelClassClasses
  case class NumberedLabelClasses(indices: NonEmptyList[Int]) extends LabelClassClasses

  implicit val encNamedLabelClasses: Encoder[NamedLabelClasses] =
    Encoder[NonEmptyList[String]].contramap(_.names)

  implicit val decNamedLabelClasses: Decoder[NamedLabelClasses] =
    Decoder[NonEmptyList[String]].map(NamedLabelClasses.apply)

  implicit val encNumberedLabelClasses: Encoder[NumberedLabelClasses] =
    Encoder[NonEmptyList[Int]].contramap(_.indices)

  implicit val decNumberedLabelClasses: Decoder[NumberedLabelClasses] =
    Decoder[NonEmptyList[Int]].map(NumberedLabelClasses.apply)

  implicit val encLabelClassClasses: Encoder[LabelClassClasses] = new Encoder[LabelClassClasses] {

    def apply(t: LabelClassClasses): Json = t match {
      case named: NamedLabelClasses       => named.asJson
      case numbered: NumberedLabelClasses => numbered.asJson
    }
  }

  implicit val eqLabelClassClasses: Eq[LabelClassClasses] = Eq.fromUniversalEquals

  implicit val decLabelClassClassels: Decoder[LabelClassClasses] = Decoder[NumberedLabelClasses].widen or
    Decoder[NamedLabelClasses].widen
}
