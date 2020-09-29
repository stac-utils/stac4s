package com.azavea.stac4s.meta

import com.azavea.stac4s.TemporalExtent

import cats.Eq
import cats.syntax.either._
import eu.timepit.refined.api.RefType
import geotrellis.vector.Geometry
import io.circe._
import io.circe.parser.decode
import io.circe.syntax._
import org.joda.time.Instant

import scala.util.Try

trait ForeignImplicits {

  // cats.Eq
  implicit val eqInstant: Eq[Instant]   = Eq.fromUniversalEquals
  implicit val eqGeometry: Eq[Geometry] = Eq.fromUniversalEquals

  // circe codecs

  implicit val encodeJodaInstant: Encoder[Instant] = Encoder[String].contramap(_.toString)

  implicit val decodeJodaInstant: Decoder[Instant] =
    Decoder[String].emap(s => Either.fromTry(Try(Instant.parse(s))).leftMap(_ => s"$s was not a valid string format"))

  implicit val encodeTemporalExtent: Encoder[TemporalExtent] = _.value.map(x => x.asJson).asJson

  implicit val decodeTemporalExtent =
    Decoder.decodeList[Option[Instant]].emap { l => RefType.applyRef[TemporalExtent](l) }

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
