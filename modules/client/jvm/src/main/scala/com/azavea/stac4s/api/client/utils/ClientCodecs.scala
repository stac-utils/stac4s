package com.azavea.stac4s.api.client.utils

import com.azavea.stac4s.jvmTypes.TemporalExtent

import cats.syntax.apply._
import cats.syntax.either._
import io.circe.{Decoder, Encoder}

import java.time.Instant

trait ClientCodecs {

  // TemporalExtent STAC API compatible serialization
  // Ported from https://github.com/azavea/franklin/
  private def stringToInstant(s: String): Either[Throwable, Instant] =
    Either.catchNonFatal(Instant.parse(s))

  private def temporalExtentToString(te: TemporalExtent): String =
    te.value match {
      case Some(start) :: Some(end) :: _ if start != end => s"${start.toString}/${end.toString}"
      case Some(start) :: Some(end) :: _ if start == end => s"${start.toString}"
      case Some(start) :: None :: _                      => s"${start.toString}/.."
      case None :: Some(end) :: _                        => s"../${end.toString}"
      case x                                             => throw new scala.MatchError(x)
    }

  private def temporalExtentFromString(str: String): Either[String, TemporalExtent] = {
    str.split("/").toList match {
      case ".." :: endString :: _ =>
        val parsedEnd = stringToInstant(endString)
        parsedEnd match {
          case Left(_)             => s"Could not decode instant: $str".asLeft
          case Right(end: Instant) => TemporalExtent(None, end).asRight
        }
      case startString :: ".." :: _ =>
        val parsedStart = stringToInstant(startString)
        parsedStart match {
          case Left(_)               => s"Could not decode instant: $str".asLeft
          case Right(start: Instant) => TemporalExtent(start, None).asRight
        }
      case startString :: endString :: _ =>
        val parsedStart = stringToInstant(startString)
        val parsedEnd   = stringToInstant(endString)
        (parsedStart, parsedEnd).tupled match {
          case Left(_)                               => s"Could not decode instant: $str".asLeft
          case Right((start: Instant, end: Instant)) => TemporalExtent(start, end).asRight
        }
      case _ =>
        Either.catchNonFatal(Instant.parse(str)) match {
          case Left(_)           => s"Could not decode instant: $str".asLeft
          case Right(t: Instant) => TemporalExtent(t, t).asRight
        }
    }
  }

  implicit lazy val encoderTemporalExtent: Encoder[TemporalExtent] =
    Encoder.encodeString.contramap[TemporalExtent](temporalExtentToString)

  implicit lazy val decoderTemporalExtent: Decoder[TemporalExtent] =
    Decoder.decodeString.emap(temporalExtentFromString)

}
