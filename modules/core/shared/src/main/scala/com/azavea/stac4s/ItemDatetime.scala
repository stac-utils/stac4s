package com.azavea.stac4s

import cats.syntax.apply._
import io.circe.{Decoder, Encoder, HCursor}

import java.time.Instant

case class PointInTime(when: Instant)

case class TimeRange(start: Instant, end: Instant)

object TimeRange {

  implicit val decTimeRange: Decoder[TimeRange] = { cursor: HCursor =>
    (cursor.get[Instant]("start_datetime"), cursor.get[Instant]("end_datetime")) mapN { TimeRange.apply }
  }

  implicit val encTimeRange: Encoder[TimeRange] =
    Encoder.forProduct3("datetime", "start_datetime", "end_datetime")(range =>
      (Option.empty[Instant], range.start, range.end)
    )
}

object PointInTime {

  implicit val decPointInTime: Decoder[PointInTime] = { cursor: HCursor =>
    cursor.get[Instant]("datetime") map { PointInTime.apply }
  }
  implicit val encPointInTime: Encoder[PointInTime] = Encoder.forProduct1("datetime")(_.when)
}
