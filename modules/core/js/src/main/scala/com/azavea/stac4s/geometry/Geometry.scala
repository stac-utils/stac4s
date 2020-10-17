package com.azavea.stac4s.geometry

import cats.Eq
import cats.syntax.either._
import cats.syntax.eq._
import cats.syntax.traverse._
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._

sealed abstract class Geometry

object Geometry {

  case class Point2d private (x: Double, y: Double) extends Geometry {
    def asCoordinateArray: List[Double] = List(x, y)
  }

  case class Polygon private (coords: List[Point2d]) extends Geometry {
    def asCoordinateArray: List[List[Double]] = coords.map(_.asCoordinateArray)
  }

  case class MultiPolygon private (polys: List[Polygon]) extends Geometry {
    def asCoordinateArray: List[List[List[Double]]] = polys.map(_.asCoordinateArray)
  }

  private def point2dFromArray(arr: List[Double]): Either[String, Point2d] = arr match {
    case longitude :: latitude :: Nil => Right(Point2d(longitude, latitude))
    case _                            => Left("Point can only be constructed from exactly two coordinates")
  }

  private def polygonFromArray(arr: List[List[Double]]): Either[String, Polygon] =
    arr traverse { point2dFromArray } flatMap { points =>
      (points.headOption, points.lastOption) match {
        case (Some(h), Some(t)) if h === t && points.size >= 4 =>
          Either.right[String, Polygon](Polygon(points))
        case _ =>
          Either.left(
            "Polyons must have at least four points with the first equal to the last"
          )
      }
    }

  private def multiPolygonFromArray(arr: List[List[List[Double]]]): Either[String, MultiPolygon] =
    arr traverse { polygonFromArray } map { MultiPolygon }

  implicit val eqPoint2d: Eq[Point2d] = Eq.fromUniversalEquals

  implicit val encPoint2d: Encoder[Point2d] = new Encoder[Point2d] {

    def apply(a: Point2d): Json =
      Map(
        "type"        -> "Point".asJson,
        "coordinates" -> List(a.x, a.y).asJson
      ).asJson
  }

  implicit val encPolygon: Encoder[Polygon] = new Encoder[Polygon] {

    // for now, I'm ignoring polygons with holes
    def apply(a: Polygon): Json =
      Map(
        "type"        -> "Polygon".asJson,
        "coordinates" -> a.asCoordinateArray.asJson
      ).asJson
  }

  implicit val encMultiPolygon: Encoder[MultiPolygon] = new Encoder[MultiPolygon] {

    def apply(a: MultiPolygon): Json =
      Map(
        "type"        -> "MultiPolygon".asJson,
        "coordinates" -> a.asCoordinateArray.asJson
      ).asJson
  }

  implicit val decPoint2d: Decoder[Point2d] = new Decoder[Point2d] {

    def apply(c: HCursor): Decoder.Result[Point2d] =
      c.get[List[Double]]("coordinates") match {
        case Right(arr) => point2dFromArray(arr).leftMap(DecodingFailure(_, c.history))
        case Left(err)  => Left(err) // re-wrap to get the correct RHS
      }
  }

  implicit val decPolygon: Decoder[Polygon] = new Decoder[Polygon] {

    def apply(c: HCursor): Decoder.Result[Polygon] =
      c.get[List[List[Double]]]("coordinates") match {
        case Right(arr) =>
          polygonFromArray(arr).leftMap(DecodingFailure(_, c.history))
        case Left(err) => Left(err) // re-wrap to get the correct RHS
      }

  }

  implicit val decMultiPolygon: Decoder[MultiPolygon] = new Decoder[MultiPolygon] {

    def apply(c: HCursor): Decoder.Result[MultiPolygon] =
      c.get[List[List[List[Double]]]]("coordinates") flatMap { arr =>
        multiPolygonFromArray(arr).leftMap(DecodingFailure(_, c.history))
      }
  }

  implicit val encGeometry: Encoder[Geometry] = deriveEncoder

  implicit val decGeometry: Decoder[Geometry] = new Decoder[Geometry] {

    def apply(c: HCursor): Decoder.Result[Geometry] =
      for {
        geomType <- c.get[String]("type")
        result <- geomType.toLowerCase match {
          case "polygon"      => Decoder[Polygon].decodeJson(c.value)
          case "point"        => Decoder[Point2d].decodeJson(c.value)
          case "multipolygon" => Decoder[MultiPolygon].decodeJson(c.value)
          case _              => Left(DecodingFailure(s"Unrecognized geometry: $geomType", c.history))
        }
      } yield result
  }

}
