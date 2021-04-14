package com.azavea.stac4s.testing

import com.azavea.stac4s.extensions.layer.StacLayer
import com.azavea.stac4s.geometry.Geometry.{MultiPolygon, Point2d, Polygon}
import com.azavea.stac4s.geometry._
import com.azavea.stac4s.jsTypes.TemporalExtent
import com.azavea.stac4s.types.CollectionType
import com.azavea.stac4s.{
  Bbox,
  Interval,
  ItemCollection,
  Proprietary,
  SpatialExtent,
  StacCollection,
  StacExtent,
  StacItem,
  StacItemAsset,
  StacLink,
  StacVersion
}

import cats.syntax.apply._
import cats.syntax.option._
import eu.timepit.refined.scalacheck.GenericInstances
import io.circe.JsonObject
import io.circe.syntax._
import org.scalacheck.cats.implicits._
import org.scalacheck.{Arbitrary, Gen}

import java.time.Instant

trait JsInstances extends GenericInstances {

  private[testing] def finiteDoubleGen: Gen[Double] = Arbitrary.arbitrary[Double].filterNot(_.isNaN)

  private[testing] def point2dGen: Gen[Point2d] = (finiteDoubleGen, finiteDoubleGen).mapN(Point2d.apply)

  private[testing] def temporalExtentGen: Gen[TemporalExtent] =
    (Gen.const(Instant.now), Gen.const(Instant.now)).tupled
      .map { case (start, end) => TemporalExtent(start, end) }

  private[testing] def stacExtentGen: Gen[StacExtent] =
    (
      TestInstances.bboxGen,
      temporalExtentGen
    ).mapN((bbox: Bbox, interval: TemporalExtent) => StacExtent(SpatialExtent(List(bbox)), Interval(List(interval))))

  /** We know for sure that we have five points, so there's no risk in calling .head */
  @SuppressWarnings(Array("TraversableHead", "UnsafeTraversableMethods")) private[testing] def polygonGen
      : Gen[Polygon] =
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

  private[testing] def stacItemShortGen: Gen[StacItem] =
    (
      nonEmptyStringGen,
      Gen.const("0.8.0"),
      Gen.const(List.empty[String]),
      Gen.const("Feature"),
      geometryGen,
      TestInstances.twoDimBboxGen,
      Gen.const(Nil),
      Gen.const(Map.empty[String, StacItemAsset]),
      Gen.option(nonEmptyStringGen),
      TestInstances.itemExtensionFieldsGen
    ).mapN(StacItem.apply)

  private[testing] def itemCollectionGen: Gen[ItemCollection] =
    (
      Gen.const("FeatureCollection"),
      Gen.const(StacVersion.unsafeFrom("1.0.0-beta.2")),
      Gen.const(Nil),
      Gen.listOf[StacItem](stacItemGen),
      Gen.listOf[StacLink](TestInstances.stacLinkGen),
      Gen.const(().asJsonObject)
    ).mapN(ItemCollection.apply)

  private[testing] def itemCollectionShortGen: Gen[ItemCollection] =
    (
      Gen.const("FeatureCollection"),
      Gen.const(StacVersion.unsafeFrom("1.0.0-beta.2")),
      Gen.const(Nil),
      Gen.listOfN[StacItem](2, stacItemGen),
      Gen.const(Nil),
      Gen.const(().asJsonObject)
    ).mapN(ItemCollection.apply)

  private[testing] def stacCollectionShortGen: Gen[StacCollection] =
    (
      Arbitrary.arbitrary[CollectionType],
      Gen.const("1.0.0-beta.2"),
      Gen.const(Nil),
      nonEmptyStringGen,
      nonEmptyStringGen.map(_.some),
      nonEmptyStringGen,
      Gen.const(Nil),
      Gen.const(Proprietary()),
      Gen.const(Nil),
      stacExtentGen,
      Gen.const(JsonObject.empty),
      Gen.const(JsonObject.empty),
      Gen.const(Nil),
      Gen.const(().asJsonObject)
    ).mapN(StacCollection.apply)

  private[testing] def stacLayerGen: Gen[StacLayer] = (
    nonEmptyAlphaRefinedStringGen,
    TestInstances.bboxGen,
    geometryGen,
    TestInstances.stacLayerPropertiesGen,
    Gen.listOfN(8, TestInstances.stacLinkGen),
    Gen.const("Feature")
  ).mapN(
    StacLayer.apply
  )

  implicit val arbItem: Arbitrary[StacItem] = Arbitrary { stacItemGen }

  val arbItemShort: Arbitrary[StacItem] = Arbitrary { stacItemShortGen }

  implicit val arbItemCollection: Arbitrary[ItemCollection] = Arbitrary {
    itemCollectionGen
  }

  val arbItemCollectionShort: Arbitrary[ItemCollection] = Arbitrary {
    itemCollectionShortGen
  }

  implicit val arbGeometry: Arbitrary[Geometry] = Arbitrary { geometryGen }

  val arbCollectionShort: Arbitrary[StacCollection] = Arbitrary { stacCollectionShortGen }

  implicit val arbStacLayer: Arbitrary[StacLayer] = Arbitrary {
    stacLayerGen
  }
}

object JsInstances extends JsInstances {}
