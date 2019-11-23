package com.azavea.stac4s

import java.time.Instant

import cats.implicits._
import com.azavea.stac4s.core.TemporalExtent
import eu.timepit.refined.api.RefType
import geotrellis.vector.{io => _, _}
import io.circe._
import io.circe.parser.{decode, parse}
import io.circe.syntax._

package object meta {

  // Stolen straight from circe docs
  implicit val decodeInstant: Decoder[Instant] = Decoder.decodeString.emap { str =>
    Either
      .catchNonFatal(Instant.parse(str))
      .leftMap(_ => "Instant")
  }

  implicit val encodeInstant: Encoder[Instant] =
    Encoder.encodeString.contramap[Instant](_.toString)

  implicit val encodeTemporalExtent =
    new Encoder[TemporalExtent] {

      def apply(t: TemporalExtent): Json = {
        t.value.map(x => x.asJson).asJson
      }
    }

  implicit val decodeTemporalExtent =
    Decoder.decodeList[Option[Instant]].emap {
      case l =>
        RefType.applyRef[TemporalExtent](l)
    }

  implicit val geometryDecoder: Decoder[Geometry] = Decoder[Json] map { js =>
    js.spaces4.parseGeoJson[Geometry]
  }

  implicit val geometryEncoder: Encoder[Geometry] = new Encoder[Geometry] {

    def apply(geom: Geometry) = {
      parse(geom.toGeoJson) match {
        case Right(js) => js
        case Left(e)   => throw e
      }
    }
  }

  implicit val decTimeRange: Decoder[(Option[Instant], Option[Instant])] = Decoder[String] map { str =>
    val components = str.replace("[", "").replace("]", "").split(",") map {
      _.trim
    }
    components match {
      case parts if parts.length == 2 =>
        val start = parts.head
        val end   = parts.drop(1).head
        (decode[Instant](start).toOption, decode[Instant](end).toOption)
      case parts if parts.length > 2 =>
        val message = s"Too many elements for temporal extent: $parts"
        throw new ParsingFailure(message, new Exception(message))
      case parts if parts.length < 2 =>
        val message = s"Too few elements for temporal extent: $parts"
        throw new ParsingFailure(message, new Exception(message))
    }
  }
}
