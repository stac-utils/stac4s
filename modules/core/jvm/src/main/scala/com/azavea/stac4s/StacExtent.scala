package com.azavea.stac4s

import com.azavea.stac4s.types._

import cats.Eq
import cats.syntax.apply._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.refined._
import io.circe.syntax._

final case class SpatialExtent(bbox: List[Bbox])

object SpatialExtent {
  implicit val encSpatialExtent: Encoder[SpatialExtent] = deriveEncoder
  implicit val decSpatialExtent: Decoder[SpatialExtent] = deriveDecoder
}

final case class Interval(interval: List[TemporalExtent], extensionFields: JsonObject = JsonObject.empty)

object Interval {
  val intervalFields = productFieldNames[Interval]

  implicit val encInterval: Encoder[Interval] = { interval: Interval =>
    val baseEncInterval: Encoder[Interval] = Encoder.forProduct1("interval")({ interval: Interval =>
      interval.interval
    })
    baseEncInterval(interval).deepMerge(interval.extensionFields.asJson)
  }

  implicit val decInterval: Decoder[Interval] = { c: HCursor =>
    (
      c.get[List[TemporalExtent]]("interval"),
      c.value.as[JsonObject]
    ) mapN { (interval: List[TemporalExtent], document: JsonObject) =>
      Interval(
        interval,
        document.filter({ case (k, _) =>
          !intervalFields.contains(k)
        })
      )
    }

  }

  implicit val eqInterval: Eq[Interval] = Eq.fromUniversalEquals
}

final case class StacExtent(
    spatial: SpatialExtent,
    temporal: Interval
)

object StacExtent {
  implicit val eqStacExtent: Eq[StacExtent]       = Eq.fromUniversalEquals
  implicit val encStacExtent: Encoder[StacExtent] = deriveEncoder
  implicit val decStacExtent: Decoder[StacExtent] = deriveDecoder
}
