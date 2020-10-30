package com.azavea.stac4s.testing

import org.scalacheck.{Arbitrary, Gen}
import cats.syntax.apply._
import cats.syntax.functor._
import org.scalacheck.cats.implicits._
import com.azavea.stac4s.ItemCollection
import io.circe.syntax._
import com.azavea.stac4s.{StacItem, StacLink, StacVersion}
import geotrellis.vector.{Geometry, Point, Polygon}

trait JvmInstances {

  private[testing] def rectangleGen: Gen[Geometry] =
    (for {
      lowerX <- Gen.choose(0.0, 1000.0)
      lowerY <- Gen.choose(0.0, 1000.0)
    } yield {
      Polygon(
        Point(lowerX, lowerY),
        Point(lowerX + 100, lowerY),
        Point(lowerX + 100, lowerY + 100),
        Point(lowerX, lowerY + 100),
        Point(lowerX, lowerY)
      )
    }).widen

  private[testing] def stacItemGen: Gen[StacItem] =
    (
      nonEmptyStringGen,
      Gen.const("0.8.0"),
      Gen.const(List.empty[String]),
      Gen.const("Feature"),
      rectangleGen,
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

  implicit val arbGeometry: Arbitrary[Geometry] = Arbitrary { rectangleGen }

}

object JvmInstances extends JvmInstances {}
