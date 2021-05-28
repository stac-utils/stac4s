package com.azavea.stac4s

import cats.kernel.Eq
import io.circe.{Decoder, DecodingFailure, Encoder, HCursor}

import java.time.Instant

case class TemporalExtent(start: Option[Instant], end: Option[Instant])

object TemporalExtent {

  def apply(start: Instant, end: Option[Instant]): TemporalExtent =
    TemporalExtent(Some(start), end)

  def apply(start: Option[Instant], end: Instant): TemporalExtent =
    TemporalExtent(start, Some(end))

  def apply(start: Instant, end: Instant): TemporalExtent =
    TemporalExtent(Some(start), Some(end))

  implicit val eqTemporalExtent: Eq[TemporalExtent] = Eq.fromUniversalEquals

  implicit val decTemporalExtent: Decoder[TemporalExtent] = { c: HCursor =>
    c.value.as[List[Option[Instant]]] flatMap {
      case h :: t :: Nil => Right(TemporalExtent(h, t))
      case _             => Left(DecodingFailure("Temporal extents must have exactly two elements", c.history))
    }
  }

  implicit val encTemporalExtent: Encoder[TemporalExtent] =
    Encoder[List[Option[Instant]]].contramap(ext => List(ext.start, ext.end))
}
