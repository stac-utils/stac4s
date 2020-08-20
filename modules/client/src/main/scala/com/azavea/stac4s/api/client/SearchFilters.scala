package com.azavea.stac4s.api.client

import com.azavea.stac4s.{Bbox, TemporalExtent}
import io.circe._
import io.circe.generic.semiauto._
import io.circe.refined._
import geotrellis.vector._
import cats.syntax.either._
import cats.syntax.apply._
import cats.instances.either._
import eu.timepit.refined.types.numeric.NonNegInt

import java.time.Instant

case class SearchFilters(
    bbox: Option[Bbox] = None,
    datetime: Option[TemporalExtent] = None,
    intersects: Option[Geometry] = None,
    collections: List[String] = Nil,
    items: List[String] = Nil,
    limit: Option[NonNegInt] = None,
    query: JsonObject = JsonObject.empty,
    next: Option[PaginationToken] = None
)

object SearchFilters {

  // TemporalExtent STAC API compatible serialization
  private def stringToInstant(s: String): Either[Throwable, Instant] =
    Either.catchNonFatal(Instant.parse(s))

  private def temporalExtentToString(te: TemporalExtent): String =
    te.value match {
      case Some(start) :: Some(end) :: _ if start != end => s"${start.toString}/${end.toString}"
      case Some(start) :: Some(end) :: _ if start == end => s"${start.toString}"
      case Some(start) :: None :: _                      => s"${start.toString}/.."
      case None :: Some(end) :: _                        => s"../${end.toString}"
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

  implicit val encoderTemporalExtent: Encoder[TemporalExtent] =
    Encoder.encodeString.contramap[TemporalExtent](temporalExtentToString)

  implicit val decoderTemporalExtent: Decoder[TemporalExtent] =
    Decoder.decodeString.emap(temporalExtentFromString)

  implicit val searchFilterDecoder: Decoder[SearchFilters] = { c =>
    for {
      bbox              <- c.downField("bbox").as[Option[Bbox]]
      datetime          <- c.downField("datetime").as[Option[TemporalExtent]]
      intersects        <- c.downField("intersects").as[Option[Geometry]]
      collectionsOption <- c.downField("collections").as[Option[List[String]]]
      itemsOption       <- c.downField("items").as[Option[List[String]]]
      limit             <- c.downField("limit").as[Option[NonNegInt]]
      query             <- c.get[Option[JsonObject]]("query")
      paginationToken   <- c.get[Option[PaginationToken]]("next")
    } yield {
      SearchFilters(
        bbox,
        datetime,
        intersects,
        collectionsOption.getOrElse(Nil),
        itemsOption.getOrElse(Nil),
        limit,
        query.getOrElse(JsonObject.empty),
        paginationToken
      )
    }
  }

  implicit val searchFilterEncoder: Encoder[SearchFilters] = deriveEncoder
}
