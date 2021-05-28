package com.azavea.stac4s.testing

import com.azavea.stac4s.extensions.layer.StacLayer
import com.azavea.stac4s.extensions.periodic.PeriodicExtent
import com.azavea.stac4s.jvmTypes.TemporalExtent
import com.azavea.stac4s.syntax._
import com.azavea.stac4s.types.CollectionType
import com.azavea.stac4s.{
  Bbox,
  Interval,
  ItemCollection,
  ItemDatetime,
  ItemProperties,
  NumericRangeSummary,
  SchemaSummary,
  SpatialExtent,
  StacAsset,
  StacCollection,
  StacExtent,
  StacItem,
  StacLink,
  StacVersion,
  StringRangeSummary,
  SummaryValue
}

import cats.syntax.apply._
import cats.syntax.functor._
import eu.timepit.refined.scalacheck.GenericInstances
import eu.timepit.refined.types.string
import geotrellis.vector.{Geometry, Point, Polygon}
import io.circe.literal._
import io.circe.syntax._
import io.circe.{Json, JsonObject}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.cats.implicits._
import org.scalacheck.{Arbitrary, Gen}
import org.threeten.extra.PeriodDuration

import scala.annotation.nowarn

import java.time.{Duration, Instant, Period}

trait JvmInstances extends GenericInstances {

  // this is parse + triple-quote instead of circe-literal json macro because
  @nowarn
  private val schema: Json = json"""
  {
  "$$schema": "http://json-schema.org/draft-07/schema#",
  "$$id": "https://schemas.stacspec.org/v1.0.0/catalog-spec/json-schema/catalog.json#",
  "title": "STAC Catalog Specification",
  "description": "This object represents Catalogs in a SpatioTemporal Asset Catalog.",
  "allOf": [
    {
      "$$ref": "#/definitions/catalog"
    }
  ],
  "definitions": {
    "catalog": {
      "title": "STAC Catalog",
      "type": "object",
      "required": [
        "stac_version",
        "type",
        "id",
        "description",
        "links"
      ],
      "properties": {
        "stac_version": {
          "title": "STAC version",
          "type": "string",
          "const": "1.0.0"
        },
        "stac_extensions": {
          "title": "STAC extensions",
          "type": "array",
          "uniqueItems": true,
          "items": {
            "title": "Reference to a JSON Schema",
            "type": "string",
            "format": "iri"
          }
        },
        "type": {
          "title": "Type of STAC entity",
          "const": "Catalog"
        },
        "id": {
          "title": "Identifier",
          "type": "string",
          "minLength": 1
        },
        "title": {
          "title": "Title",
          "type": "string"
        },
        "description": {
          "title": "Description",
          "type": "string",
          "minLength": 1
        },
        "links": {
          "title": "Links",
          "type": "array",
          "items": {
            "$$ref": "#/definitions/link"
          }
        }
      }
    },
    "link": {
      "type": "object",
      "required": [
        "rel",
        "href"
      ],
      "properties": {
        "href": {
          "title": "Link reference",
          "type": "string",
          "format": "iri-reference",
          "minLength": 1
        },
        "rel": {
          "title": "Link relation type",
          "type": "string",
          "minLength": 1
        },
        "type": {
          "title": "Link type",
          "type": "string"
        },
        "title": {
          "title": "Link title",
          "type": "string"
        }
      }
    }
  }
}"""

  // generate way fewer schema summaries, since that's just a const
  private[testing] def summaryValueGen: Gen[SummaryValue] = Gen.frequency(
    (1, Gen.const(SchemaSummary(schema))),
    (5, (TestInstances.finiteDoubleGen, TestInstances.finiteDoubleGen) mapN { NumericRangeSummary(_, _) }),
    (
      5,
      (nonEmptyAlphaRefinedStringGen, nonEmptyAlphaRefinedStringGen) mapN {
        StringRangeSummary(_, _)
      }
    )
  )

  private[testing] def summariesGen: Gen[Map[string.NonEmptyString, SummaryValue]] =
    Gen.mapOfN(3, (nonEmptyAlphaRefinedStringGen, summaryValueGen).tupled)

  private[testing] def temporalExtentGen: Gen[TemporalExtent] = {
    (arbitrary[Instant], arbitrary[Instant]).tupled
      .map { case (start, end) =>
        TemporalExtent(start, end)
      }
  }

  private[testing] def intervalGen: Gen[Interval] =
    (periodicExtentGen, temporalExtentGen).tupled flatMap { case (periodicity, temporalExtent) =>
      Gen.oneOf(
        Gen.const(Interval(List(temporalExtent))),
        Gen.const(Interval(List(temporalExtent)).addExtensionFields(periodicity))
      )
    }

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

  private[testing] def stacItemShortGen: Gen[StacItem] =
    (
      nonEmptyStringGen,
      Gen.const("1.0.0-rc2"),
      Gen.const(List.empty[String]),
      Gen.const("Feature"),
      rectangleGen,
      TestInstances.twoDimBboxGen,
      Gen.const(Nil),
      Gen.const(Map.empty[String, StacAsset]),
      Gen.option(nonEmptyStringGen),
      itemPropertiesGen
    ).mapN(StacItem.apply)

  private[testing] def itemCollectionGen: Gen[ItemCollection] =
    (
      Gen.const("FeatureCollection"),
      Gen.const(StacVersion.unsafeFrom("1.0.0-rc2")),
      Gen.const(Nil),
      Gen.listOf[StacItem](stacItemGen),
      Gen.listOf[StacLink](TestInstances.stacLinkGen),
      Gen.const(().asJsonObject)
    ).mapN(ItemCollection.apply)

  private[testing] def itemCollectionShortGen: Gen[ItemCollection] =
    (
      Gen.const("FeatureCollection"),
      Gen.const(StacVersion.unsafeFrom("1.0.0-rc2")),
      Gen.const(Nil),
      Gen.listOf[StacItem](stacItemGen),
      Gen.const(Nil),
      Gen.const(().asJsonObject)
    ).mapN(ItemCollection.apply)

  private[testing] def stacExtentGen: Gen[StacExtent] =
    (
      TestInstances.bboxGen,
      temporalExtentGen
    ).mapN((bbox: Bbox, interval: TemporalExtent) => StacExtent(SpatialExtent(List(bbox)), Interval(List(interval))))

  private[testing] def stacCollectionGen: Gen[StacCollection] =
    (
      Arbitrary.arbitrary[CollectionType],
      nonEmptyStringGen,
      possiblyEmptyListGen(nonEmptyStringGen),
      nonEmptyStringGen,
      Gen.option(nonEmptyStringGen),
      nonEmptyStringGen,
      possiblyEmptyListGen(nonEmptyStringGen),
      TestInstances.stacLicenseGen,
      possiblyEmptyListGen(TestInstances.stacProviderGen),
      stacExtentGen,
      summariesGen,
      Gen.const(JsonObject.fromMap(Map.empty)),
      possiblyEmptyListGen(TestInstances.stacLinkGen),
      Gen.option(TestInstances.assetMapGen),
      TestInstances.collectionExtensionFieldsGen
    ).mapN(StacCollection.apply)

  private[testing] def stacCollectionShortGen: Gen[StacCollection] =
    (
      Arbitrary.arbitrary[CollectionType],
      nonEmptyStringGen,
      possiblyEmptyListGen(nonEmptyStringGen),
      nonEmptyStringGen,
      Gen.option(nonEmptyStringGen),
      nonEmptyStringGen,
      Gen.const(Nil),
      TestInstances.stacLicenseGen,
      Gen.const(Nil),
      stacExtentGen,
      summariesGen,
      Gen.const(JsonObject.fromMap(Map.empty)),
      Gen.const(Nil),
      Gen.option(Gen.const(Map.empty[String, StacAsset])),
      Gen.const(().asJsonObject)
    ).mapN(StacCollection.apply)

  private[testing] def stacLayerGen: Gen[StacLayer] = (
    nonEmptyAlphaRefinedStringGen,
    TestInstances.bboxGen,
    rectangleGen,
    TestInstances.stacLayerPropertiesGen,
    Gen.listOfN(8, TestInstances.stacLinkGen),
    Gen.const("Feature")
  ).mapN(
    StacLayer.apply
  )

  private[testing] def periodDurationGen: Gen[PeriodDuration] = for {
    years  <- Gen.choose(0, 100)
    months <- Gen.choose(0, 12)
    days   <- Gen.choose(0, 100)
    // minutes is an Int because with a Long we overflow during the test.
    // since Long.MaxValue is a suuuuuuuper unlikely quantity of minutes in a
    // duration, I'm declaring this More or Less Fine™
    minutes <- arbitrary[Int]
  } yield PeriodDuration.of(
    Period.of(years, months, days),
    Duration.ofMinutes(minutes.toLong)
  )

  private[testing] def periodicExtentGen: Gen[PeriodicExtent] = (
    periodDurationGen
  ) map {
    PeriodicExtent.apply
  }

  private def itemDateTimeGen: Gen[ItemDatetime] = Gen.oneOf[ItemDatetime](
    instantGen map { ItemDatetime.PointInTime },
    (instantGen, instantGen) mapN {
      case (i1, i2) if i2.isAfter(i1) => ItemDatetime.TimeRange(i1, i2)
      case (i1, i2)                   => ItemDatetime.TimeRange(i2, i1)
    }
  )

  private def itemPropertiesGen: Gen[ItemProperties] = (
    itemDateTimeGen,
    Gen.option(nonEmptyAlphaRefinedStringGen),
    Gen.option(nonEmptyAlphaRefinedStringGen),
    Gen.option(instantGen),
    Gen.option(instantGen),
    Gen.option(TestInstances.stacLicenseGen),
    Gen.option(nonEmptyListGen(TestInstances.stacProviderGen)),
    Gen.option(nonEmptyAlphaRefinedStringGen),
    Gen.option(nonEmptyListGen(nonEmptyAlphaRefinedStringGen)),
    Gen.option(nonEmptyAlphaRefinedStringGen),
    Gen.option(nonEmptyAlphaRefinedStringGen),
    Gen.option(TestInstances.finiteDoubleGen),
    TestInstances.itemExtensionFieldsGen
  ) mapN { ItemProperties.apply }

  private[testing] def stacItemGen: Gen[StacItem] =
    (
      nonEmptyStringGen,
      Gen.const("1.0.0-rc2"),
      Gen.const(List.empty[String]),
      Gen.const("Feature"),
      rectangleGen,
      TestInstances.twoDimBboxGen,
      nonEmptyListGen(TestInstances.stacLinkGen) map { _.toList },
      TestInstances.assetMapGen,
      Gen.option(nonEmptyStringGen),
      itemPropertiesGen
    ).mapN(StacItem.apply)

  implicit val arbItem: Arbitrary[StacItem] = Arbitrary { stacItemGen }

  val arbItemShort: Arbitrary[StacItem] = Arbitrary { stacItemShortGen }

  implicit val arbItemCollection: Arbitrary[ItemCollection] = Arbitrary {
    itemCollectionGen
  }

  val arbItemCollectionShort: Arbitrary[ItemCollection] = Arbitrary {
    itemCollectionShortGen
  }

  implicit val arbGeometry: Arbitrary[Geometry] = Arbitrary { rectangleGen }

  implicit val arbInstant: Arbitrary[Instant] = Arbitrary { instantGen }

  implicit val arbCollection: Arbitrary[StacCollection] = Arbitrary {
    stacCollectionGen
  }

  val arbCollectionShort: Arbitrary[StacCollection] = Arbitrary { stacCollectionShortGen }

  implicit val arbStacExtent: Arbitrary[StacExtent] = Arbitrary {
    stacExtentGen
  }

  implicit val arbTemporalExtent: Arbitrary[TemporalExtent] = Arbitrary {
    temporalExtentGen
  }

  implicit val arbStaclayer: Arbitrary[StacLayer] = Arbitrary {
    stacLayerGen
  }

  implicit val arbPeriodDuration: Arbitrary[PeriodDuration] = Arbitrary {
    periodDurationGen
  }

  implicit val arbPeriodicExtent: Arbitrary[PeriodicExtent] = Arbitrary {
    periodicExtentGen
  }

  implicit val arbInterval: Arbitrary[Interval] = Arbitrary {
    intervalGen
  }

  implicit val arbItemDatetime: Arbitrary[ItemDatetime] = Arbitrary {
    itemDateTimeGen
  }

  implicit val arbItemProperties: Arbitrary[ItemProperties] = Arbitrary {
    itemPropertiesGen
  }

  implicit val arbSummaryValue: Arbitrary[SummaryValue] = Arbitrary {
    summaryValueGen
  }
}

object JvmInstances extends JvmInstances {}
