package com.azavea.stac4s.meta

import com.azavea.stac4s.TemporalExtent

import cats.Eq
import eu.timepit.refined.api.RefType
import geotrellis.vector.Geometry
import io.circe._
import io.circe.parser.decode
import io.circe.syntax._

import java.time.ZonedDateTime

trait ForeignImplicits {

  // cats.Eq
  implicit val eqZonedDateTime: Eq[ZonedDateTime] = Eq.fromUniversalEquals
  implicit val eqGeometry: Eq[Geometry]           = Eq.fromUniversalEquals

  // circe codecs

  implicit val encodeTemporalExtent: Encoder[TemporalExtent] = _.value.map(x => x.asJson).asJson

  implicit val decodeTemporalExtent =
    Decoder.decodeList[Option[ZonedDateTime]].emap { l => RefType.applyRef[TemporalExtent](l) }

  implicit val decTimeRange: Decoder[(Option[ZonedDateTime], Option[ZonedDateTime])] = Decoder[String] map { str =>
    val components = str.replace("[", "").replace("]", "").split(",") map {
      _.trim
    }
    components match {
      case parts if parts.length == 2 =>
        val start = parts.head
        val end   = parts.drop(1).head
        (decode[ZonedDateTime](start).toOption, decode[ZonedDateTime](end).toOption)
      case parts if parts.length > 2 =>
        val message = s"Too many elements for temporal extent: $parts"
        throw new ParsingFailure(message, new Exception(message))
      case parts if parts.length < 2 =>
        val message = s"Too few elements for temporal extent: $parts"
        throw new ParsingFailure(message, new Exception(message))
    }
  }

}
