package com.azavea.stac4s.testing

import com.azavea.stac4s.geometry.Geometry.{MultiPolygon, Point2d, Polygon}
import com.azavea.stac4s.geometry._
import com.azavea.stac4s.{ItemCollection, StacItem, StacLink, StacVersion}

import cats.syntax.apply._
import io.circe.syntax._
import org.scalacheck.cats.implicits._
import org.scalacheck.{Arbitrary, Gen}

trait JsInstances {

  private[testing] def finiteDoubleGen: Gen[Double] = Arbitrary.arbitrary[Double].filterNot(_.isNaN)
  private[testing] def point2dGen: Gen[Point2d]     = (finiteDoubleGen, finiteDoubleGen).mapN(Point2d.apply)

  private[testing] def polygonGen: Gen[Polygon] =
    Gen.listOfN(5, point2dGen).map(points => Polygon(points :+ points.head))
  private[testing] def multipolygonGen: Gen[MultiPolygon] = Gen.listOfN(3, polygonGen).map(MultiPolygon.apply)

  private[testing] def geometryGen: Gen[Geometry] = Gen.oneOf(
    point2dGen,
    polygonGen,
    multipolygonGen
  )

  private[testing] def stacItemGen: Gen[StacItem] =
    (
      nonEmptyStringGen,
      Gen.const("0.8.0"),
      Gen.const(List.empty[String]),
      Gen.const("Feature"),
      geometryGen,
      TestInstances.twoDimBboxGen,
      nonEmptyListGen(TestInstances.stacLinkGen) map { _.toList },
      Gen.nonEmptyMap((nonEmptyStringGen, TestInstances.cogAssetGen).tupled),
      Gen.option(nonEmptyStringGen),
      TestInstances.itemExtensionFieldsGen
    ).mapN(StacItem.apply)

  private[testing] def itemCollectionGen: Gen[ItemCollection] =
    (
      Gen.const("FeatureCollection"),
      Gen.const(StacVersion.unsafeFrom("0.9.0")),
      Gen.const(Nil),
      Gen.listOf[StacItem](stacItemGen),
      Gen.listOf[StacLink](TestInstances.stacLinkGen),
      Gen.const(().asJsonObject)
    ).mapN(ItemCollection.apply)

  implicit val arbItem: Arbitrary[StacItem] = Arbitrary { stacItemGen }

  implicit val arbItemCollection: Arbitrary[ItemCollection] = Arbitrary {
    itemCollectionGen
  }

  implicit val arbGeometry: Arbitrary[Geometry] = Arbitrary { geometryGen }
}

object JsInstances extends JsInstances {}
