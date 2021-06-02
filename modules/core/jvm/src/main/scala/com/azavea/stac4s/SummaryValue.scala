package com.azavea.stac4s

import cats.kernel.Eq
import cats.syntax.all._
import eu.timepit.refined.types.string
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.refined._
import io.circe.schema.Schema
import io.circe.{Decoder, DecodingFailure, Encoder, HCursor, Json}

import scala.util.Try

sealed abstract class SummaryValue

case class StringRangeSummary(
    minimum: string.NonEmptyString,
    maximum: string.NonEmptyString
) extends SummaryValue

case class NumericRangeSummary(
    minimum: Double,
    maximum: Double
) extends SummaryValue

// This constructor isn't exported because not all JSON is valid JSON Schema
case class SchemaSummary private[stac4s] (
    underlying: Json
) extends SummaryValue

object SummaryValue {

  implicit val eqSummaryValue: Eq[SummaryValue] = Eq.fromUniversalEquals

  val decStringRangeSummary: Decoder[StringRangeSummary] = deriveDecoder
  val encStringRangeSummary: Encoder[StringRangeSummary] = deriveEncoder

  val decNumericRangeSummary: Decoder[NumericRangeSummary] = deriveDecoder
  val encNumericRangeSummary: Encoder[NumericRangeSummary] = deriveEncoder

  // treat this decoder as a smart constructor, since we can't
  // decode or encode the schema directly
  val decSchemaSummary: Decoder[SchemaSummary] = { c: HCursor =>
    Either
      .fromTry(Try {
        Schema.load(c.value)
      })
      .leftMap(t => DecodingFailure(t.getMessage, c.history))
      .map(_ => SchemaSummary(c.value))
  }

  val encSchemaSummary: Encoder[SchemaSummary] = { _.underlying }

  // more horrible type inference 🙄, so had to annotate the first one
  implicit val decSummaryValue: Decoder[SummaryValue] =
    decStringRangeSummary.widen[SummaryValue] or decNumericRangeSummary.widen or decSchemaSummary.widen

  implicit val encSummaryValue: Encoder[SummaryValue] = {
    case summ: StringRangeSummary =>
      encStringRangeSummary(summ)
    case summ: NumericRangeSummary =>
      encNumericRangeSummary(summ)
    case summ: SchemaSummary =>
      encSchemaSummary(summ)
  }
}
