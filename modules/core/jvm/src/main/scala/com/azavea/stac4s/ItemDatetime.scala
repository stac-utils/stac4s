package com.azavea.stac4s

import cats.syntax.apply._
import cats.syntax.functor._
import io.circe.{Decoder, Encoder, HCursor}
import io.circe.syntax._

import java.time.Instant
import cats.kernel.Eq

sealed abstract class ItemDatetime

object ItemDatetime {

  case class PointInTime(when: Instant) extends ItemDatetime

  case class TimeRange(start: Instant, end: Instant) extends ItemDatetime

  implicit val eqItemDatetime: Eq[ItemDatetime] = Eq.fromUniversalEquals

  implicit val decPointInTime: Decoder[PointInTime] = { cursor: HCursor =>
    cursor.get[Instant]("datetime") map { PointInTime }
  }

  implicit val decTimeRange: Decoder[TimeRange] = { cursor: HCursor =>
    (cursor.get[Instant]("start_datetime"), cursor.get[Instant]("end_datetime")) mapN { TimeRange }
  }

  implicit val decItemDatetime: Decoder[ItemDatetime] = decPointInTime.widen or decTimeRange.widen

  implicit val encPointInTime: Encoder[PointInTime] = Encoder.forProduct1("datetime")(_.when)

  implicit val encTimeRange: Encoder[TimeRange] =
    Encoder.forProduct3("datetime", "start_datetime", "end_datetime")(range =>
      (Option.empty[Instant], range.start, range.end)
    )

  implicit val encItemDateTime: Encoder[ItemDatetime] = {
    case pit @ PointInTime(_) => pit.asJson
    case tr @ TimeRange(_, _) => tr.asJson
  }
}
