package com.azavea.stac4s

import cats.Eq
import cats.kernel.Semigroup
import cats.syntax.either._
import cats.syntax.functor._
import geotrellis.vector.{Extent, ExtentRangeError}
import io.circe._
import io.circe.syntax._

sealed trait Bbox {
  val xmin: Double
  val ymin: Double
  val xmax: Double
  val ymax: Double
  val toList: List[Double]

  val toExtent: Either[String, Extent] =
    try {
      Either.right(Extent(xmin, ymin, xmax, ymax))
    } catch {
      case e: ExtentRangeError => Either.left(e.toString)
    }

  def union(other: Bbox): Bbox
}

final case class TwoDimBbox(xmin: Double, ymin: Double, xmax: Double, ymax: Double) extends Bbox {
  val toList = List(xmin, ymin, xmax, ymax)

  def union(other: Bbox): Bbox = other match {
    case TwoDimBbox(otherXmin, otherYmin, otherXmax, otherYmax) =>
      TwoDimBbox(otherXmin min xmin, otherYmin min ymin, otherXmax max xmax, otherYmax max ymax)
    case ThreeDimBbox(otherXmin, otherYmin, zmin, otherXmax, otherYmax, zmax) =>
      ThreeDimBbox(otherXmin min xmin, otherYmin min ymin, zmin, otherXmax max xmax, otherYmax max ymax, zmax)
  }
}

final case class ThreeDimBbox(
    xmin: Double,
    ymin: Double,
    zmin: Double,
    xmax: Double,
    ymax: Double,
    zmax: Double
) extends Bbox {
  val toList = List(xmin, ymin, zmin, xmax, ymax, zmax)

  def union(other: Bbox): Bbox = other match {
    case TwoDimBbox(otherXmin, otherYmin, otherXmax, otherYmax) =>
      ThreeDimBbox(otherXmin min xmin, otherYmin min ymin, zmin, otherXmax max xmax, otherYmax max ymax, zmax)
    case ThreeDimBbox(otherXmin, otherYmin, otherZmin, otherXmax, otherYmax, otherZmax) =>
      ThreeDimBbox(
        otherXmin min xmin,
        otherYmin min ymin,
        otherZmin min zmin,
        otherXmax max xmax,
        otherYmax max ymax,
        otherZmax max zmax
      )
  }
}

object TwoDimBbox {

  implicit val eqTwoDimBbox: Eq[TwoDimBbox] = Eq.fromUniversalEquals

  implicit val decoderTwoDBox: Decoder[TwoDimBbox] =
    Decoder.decodeList[Double].emap {
      case twodim if twodim.length == 4 =>
        Either.right(TwoDimBbox(twodim(0), twodim(1), twodim(2), twodim(3)))
      case other =>
        Either.left(
          s"Incorrect number of values for 2d box - found ${other.length}, expected 4"
        )
    }

  implicit val encoderTwoDimBbox: Encoder[TwoDimBbox] = _.toList.asJson
}

object ThreeDimBbox {

  implicit val eqThreeDimBbox: Eq[ThreeDimBbox] = Eq.fromUniversalEquals

  implicit val decoderThreeDimBox: Decoder[ThreeDimBbox] =
    Decoder.decodeList[Double].emap {
      case threeDim if threeDim.length == 6 =>
        Either.right(
          ThreeDimBbox(
            threeDim(0),
            threeDim(1),
            threeDim(2),
            threeDim(3),
            threeDim(4),
            threeDim(5)
          )
        )
      case other =>
        Either.left(
          s"Incorrect number of values for 2d box - found ${other.length}, expected 4"
        )
    }

  implicit val encoderThreeDimBbox: Encoder[ThreeDimBbox] = _.toList.asJson
}

object Bbox {

  implicit val encoderBbox: Encoder[Bbox] = {
    case two: TwoDimBbox     => two.asJson
    case three: ThreeDimBbox => three.asJson
  }

  implicit val decoderBbox: Decoder[Bbox] = Decoder[TwoDimBbox].widen or Decoder[ThreeDimBbox].widen

  implicit val eqBbox: Eq[Bbox] = Eq.fromUniversalEquals

  implicit val semigroupBbox: Semigroup[Bbox] = Semigroup.instance((_ union _))

}
