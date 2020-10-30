package com.azavea.stac4s.testing

import com.azavea.stac4s.geometry._

import org.scalacheck.{Arbitrary, Gen}
import cats.syntax.apply._
import org.scalacheck.cats.implicits._
import com.azavea.stac4s.ItemCollection
import io.circe.syntax._
import com.azavea.stac4s.{StacItem, StacLink, StacVersion}
import com.azavea.stac4s.geometry.Geometry.Point2d
import com.azavea.stac4s.geometry.Geometry.Polygon
import com.azavea.stac4s.geometry.Geometry.MultiPolygon

trait JsInstances {

  private[testing] def point2dGen: Gen[Point2d]           = ???
  private[testing] def polygonGen: Gen[Polygon]           = ???
  private[testing] def multipolygonGen: Gen[MultiPolygon] = ???

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
