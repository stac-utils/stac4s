package com.azavea.stac4s.testing

import com.azavea.stac4s._
import com.azavea.stac4s.extensions.asset._
import com.azavea.stac4s.extensions.eo._
import com.azavea.stac4s.extensions.label.LabelClassClasses._
import com.azavea.stac4s.extensions.label._
import com.azavea.stac4s.extensions.layer.{LayerItemExtension, StacLayerProperties}
import com.azavea.stac4s.types.CatalogType

import cats.syntax.apply._
import cats.syntax.functor._
import enumeratum.scalacheck._
import eu.timepit.refined.scalacheck.{GenericInstances, NumericInstances}
import eu.timepit.refined.types.numeric.PosDouble
import io.circe.JsonObject
import io.circe.syntax._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck._
import org.scalacheck.cats.implicits._

trait TestInstances extends NumericInstances with GenericInstances {

  private[testing] def collectionExtensionFieldsGen: Gen[JsonObject] =
    Gen.const(().asJsonObject)

  private[testing] def itemExtensionFieldsGen: Gen[JsonObject] = Gen.oneOf(
    Gen.const(().asJsonObject),
    labelExtensionPropertiesGen map { _.asJsonObject },
    layerPropertiesGen map { _.asJsonObject },
    eoItemExtensionGen map { _.asJsonObject }
  )

  private[testing] def labelLinkExtensionGen: Gen[LabelLinkExtension] =
    nonEmptyListGen(nonEmptyStringGen).map(LabelLinkExtension.apply)

  private[testing] def linkExtensionFields: Gen[JsonObject] = Gen.oneOf(
    Gen.const(().asJsonObject),
    labelLinkExtensionGen.map(_.asJsonObject)
  )

  private[testing] def eoAssetExtensionGen: Gen[EOAssetExtension] =
    nonEmptyListGen(bandGen).map(EOAssetExtension.apply)

  private[testing] def assetExtensionFieldsGen: Gen[JsonObject] = Gen.oneOf(
    Gen.const(().asJsonObject),
    eoAssetExtensionGen.map(_.asJsonObject)
  )

  private[testing] def mediaTypeGen: Gen[StacMediaType] = Gen.oneOf(
    Gen.const(`image/geotiff`),
    Gen.const(`image/cog`),
    Gen.const(`image/jp2`),
    Gen.const(`image/png`),
    Gen.const(`image/jpeg`),
    Gen.const(`text/xml`),
    Gen.const(`text/html`),
    Gen.const(`application/xml`),
    Gen.const(`application/json`),
    Gen.const(`text/plain`),
    Gen.const(`application/geo+json`),
    Gen.const(`application/geopackage+sqlite3`),
    Gen.const(`application/x-hdf5`),
    Gen.const(`application/x-hdf`),
    nonEmptyStringGen map VendorMediaType.apply
  )

  private[testing] def linkTypeGen: Gen[StacLinkType] = Gen.oneOf(
    Gen.const(StacLinkType.Self),
    Gen.const(StacLinkType.StacRoot),
    Gen.const(StacLinkType.Parent),
    Gen.const(StacLinkType.Child),
    Gen.const(StacLinkType.Item),
    Gen.const(StacLinkType.Items),
    Gen.const(StacLinkType.Source),
    Gen.const(StacLinkType.Collection),
    Gen.const(StacLinkType.License),
    Gen.const(StacLinkType.Alternate),
    Gen.const(StacLinkType.DescribedBy),
    Gen.const(StacLinkType.Next),
    Gen.const(StacLinkType.Prev),
    Gen.const(StacLinkType.ServiceDesc),
    Gen.const(StacLinkType.ServiceDoc),
    Gen.const(StacLinkType.Conformance),
    Gen.const(StacLinkType.Data),
    Gen.const(StacLinkType.LatestVersion),
    Gen.const(StacLinkType.PredecessorVersion),
    Gen.const(StacLinkType.SuccessorVersion),
    Gen.const(StacLinkType.DerivedFrom),
    Gen.const(StacLinkType.Via),
    Gen.const(StacLinkType.Canonical),
    nonEmptyStringGen map StacLinkType.VendorLinkType.apply
  )

  private[testing] def assetRoleGen: Gen[StacAssetRole] = Gen.oneOf(
    Gen.const(StacAssetRole.Thumbnail),
    Gen.const(StacAssetRole.Overview),
    Gen.const(StacAssetRole.Data),
    Gen.const(StacAssetRole.Metadata),
    nonEmptyStringGen map StacAssetRole.VendorAsset.apply
  )

  private[testing] def providerRoleGen: Gen[StacProviderRole] = Gen.oneOf(
    Licensor,
    Producer,
    Processor,
    Host
  )

  private[testing] def finiteDoubleGen: Gen[Double] =
    arbitrary[Double].filter(java.lang.Double.isFinite)

  private[testing] def twoDimBboxGen: Gen[TwoDimBbox] =
    (for {
      lowerX <- finiteDoubleGen
      lowerY <- finiteDoubleGen
    } yield {
      TwoDimBbox(
        lowerX,
        lowerY,
        lowerX + 100,
        lowerY + 100
      )
    })

  private def spdxGen: Gen[SPDX] =
    arbitrary[SpdxId] map { SPDX.apply }

  private[testing] def proprietaryGen: Gen[Proprietary] = Gen.const(Proprietary())

  private[testing] def stacLicenseGen: Gen[StacLicense] = Gen.oneOf(spdxGen, proprietaryGen)

  private[testing] def threeDimBboxGen: Gen[ThreeDimBbox] =
    (for {
      lowerX <- finiteDoubleGen
      lowerY <- finiteDoubleGen
      lowerZ <- finiteDoubleGen
    } yield {
      ThreeDimBbox(
        lowerX,
        lowerY,
        lowerZ,
        lowerX + 100,
        lowerY + 100,
        lowerZ + 100
      )
    })

  private[testing] def bboxGen: Gen[Bbox] =
    Gen.oneOf(twoDimBboxGen.widen, threeDimBboxGen.widen)

  private[testing] def stacLinkGen: Gen[StacLink] =
    (
      nonEmptyStringGen,
      Gen.const(StacLinkType.Self), // self link type is required by TMS reification
      Gen.option(mediaTypeGen),
      Gen.option(nonEmptyStringGen),
      linkExtensionFields
    ).mapN(StacLink.apply)

  private[testing] def stacProviderGen: Gen[StacProvider] =
    (
      nonEmptyStringGen,
      Gen.option(nonEmptyStringGen),
      possiblyEmptyListGen(providerRoleGen),
      Gen.option(nonEmptyStringGen)
    ).mapN(StacProvider.apply)

  private[testing] def StacAssetGen: Gen[StacAsset] =
    (
      nonEmptyStringGen,
      Gen.option(nonEmptyStringGen),
      Gen.option(nonEmptyStringGen),
      Gen.containerOf[Set, StacAssetRole](assetRoleGen),
      Gen.option(mediaTypeGen),
      assetExtensionFieldsGen
    ) mapN {
      StacAsset.apply
    }

  private[testing] def stacCollectionAssetGen: Gen[StacCollectionAsset] =
    (
      nonEmptyStringGen,
      Gen.option(nonEmptyStringGen),
      Gen.containerOf[Set, StacAssetRole](assetRoleGen) map { _.toList },
      mediaTypeGen
    ).mapN {
      StacCollectionAsset.apply
    }

  // Only do COGs for now, since we don't handle anything else in the example server.
  // As more types of stac items are supported, relax this assumption
  private[testing] def cogAssetGen: Gen[StacAsset] =
    StacAssetGen map { asset => asset.copy(_type = Some(`image/cog`)) }

  private[testing] def stacCatalogGen: Gen[StacCatalog] =
    (
      arbitrary[CatalogType],
      nonEmptyStringGen,
      possiblyEmptyListGen(nonEmptyStringGen),
      nonEmptyStringGen,
      Gen.option(nonEmptyStringGen),
      nonEmptyStringGen,
      possiblyEmptyListGen(stacLinkGen),
      Gen.const(().asJsonObject),
      Gen.const(().asJsonObject)
    ).mapN(StacCatalog.apply)

  private[testing] def labelClassNameGen: Gen[LabelClassName] =
    Gen.option(nonEmptyStringGen) map {
      case Some(s) => LabelClassName.VectorName(s.toLowerCase)
      case None    => LabelClassName.Raster
    }

  private[testing] def namedLabelClassesGen: Gen[LabelClassClasses] =
    nonEmptyListGen(nonEmptyStringGen) map { NamedLabelClasses.apply }

  private[testing] def numberedLabelClassesGen: Gen[LabelClassClasses] =
    nonEmptyListGen(arbitrary[Int]) map { NumberedLabelClasses.apply }

  private[testing] def labelClassClassesGen: Gen[LabelClassClasses] =
    Gen.oneOf(
      namedLabelClassesGen,
      numberedLabelClassesGen
    )

  private[testing] def labelClassGen: Gen[LabelClass] =
    (
      labelClassNameGen,
      labelClassClassesGen
    ).mapN(LabelClass.apply)

  private[testing] def labelCountGen: Gen[LabelCount] =
    (
      nonEmptyStringGen,
      arbitrary[Int]
    ).mapN(LabelCount.apply)

  private[testing] def labelStatsGen: Gen[LabelStats] =
    (
      nonEmptyStringGen,
      finiteDoubleGen
    ).mapN(LabelStats.apply)

  private[testing] def labelOverviewWithCounts: Gen[LabelOverview] =
    (
      nonEmptyStringGen,
      possiblyEmptyListGen(labelCountGen)
    ).mapN((key: String, counts: List[LabelCount]) => LabelOverview(key, counts, Nil))

  private[testing] def labelOverviewWithStats: Gen[LabelOverview] =
    (
      nonEmptyStringGen,
      possiblyEmptyListGen(labelStatsGen)
    ).mapN((key: String, stats: List[LabelStats]) => LabelOverview(key, Nil, stats))

  private[testing] def labelOverviewGen: Gen[LabelOverview] = Gen.oneOf(
    labelOverviewWithCounts,
    labelOverviewWithStats
  )

  private[testing] def labelTaskGen: Gen[LabelTask] =
    Gen.oneOf(
      Gen.const(LabelTask.Classification),
      Gen.const(LabelTask.Detection),
      Gen.const(LabelTask.Regression),
      Gen.const(LabelTask.Segmentation),
      nonEmptyStringGen map LabelTask.VendorTask.apply
    )

  private[testing] def labelMethodGen: Gen[LabelMethod] = Gen.oneOf(
    Gen.oneOf(
      Gen.const(LabelMethod.Automatic),
      Gen.const(LabelMethod.Manual),
      nonEmptyStringGen map LabelMethod.VendorMethod.apply
    ),
    nonEmptyStringGen map LabelMethod.fromString
  )

  private[testing] def labelTypeGen: Gen[LabelType] = Gen.oneOf(
    LabelType.Vector,
    LabelType.Raster
  )

  private[testing] def labelPropertiesGen: Gen[LabelProperties] =
    Gen.option(possiblyEmptyListGen(nonEmptyStringGen)).map(LabelProperties.fromOption)

  private[testing] def layerPropertiesGen: Gen[LayerItemExtension] =
    nonEmptyListGen(nonEmptyAlphaRefinedStringGen) map { LayerItemExtension.apply }

  private[testing] def labelExtensionPropertiesGen: Gen[LabelItemExtension] =
    (
      labelPropertiesGen,
      possiblyEmptyListGen(labelClassGen),
      nonEmptyStringGen,
      labelTypeGen,
      possiblyEmptyListGen(labelTaskGen),
      possiblyEmptyListGen(labelMethodGen),
      possiblyEmptyListGen(labelOverviewGen)
    ).mapN(LabelItemExtension.apply)

  private[testing] def bandGen: Gen[Band] =
    (
      nonEmptyAlphaRefinedStringGen,
      Gen.option(nonEmptyAlphaRefinedStringGen),
      Gen.option(nonEmptyAlphaRefinedStringGen),
      Gen.option(arbitrary[PosDouble]),
      Gen.option(arbitrary[PosDouble])
    ).mapN(Band.apply)

  private[testing] def eoItemExtensionGen: Gen[EOItemExtension] =
    (
      nonEmptyListGen(bandGen),
      Gen.option(arbitrary[Percentage])
    ).mapN(EOItemExtension.apply)

  private[testing] def stacLayerPropertiesGen: Gen[StacLayerProperties] =
    (
      instantGen,
      instantGen
    ).mapN {
      case (inst1, inst2) if inst1.isBefore(inst2) =>
        StacLayerProperties(inst1, inst2)
      case (inst1, inst2) => StacLayerProperties(inst2, inst1)
    }

  private[testing] def assetMapGen: Gen[Map[String, StacAsset]] =
    Gen.oneOf(
      Gen.const(Map.empty[String, StacAsset]),
      Gen.listOfN(5, (nonEmptyStringGen, cogAssetGen).tupled) map { Map(_: _*) }
    )

  implicit val arbMediaType: Arbitrary[StacMediaType] = Arbitrary {
    mediaTypeGen
  }

  implicit val arbLinkType: Arbitrary[StacLinkType] = Arbitrary { linkTypeGen }

  implicit val arbProviderRole: Arbitrary[StacProviderRole] = Arbitrary {
    providerRoleGen
  }

  implicit val arbStacProvider: Arbitrary[StacProvider] = Arbitrary {
    stacProviderGen
  }

  implicit val arbStacAsset: Arbitrary[StacAsset] = Arbitrary { StacAssetGen }

  implicit val arbCollectionAsset: Arbitrary[StacCollectionAsset] = Arbitrary { stacCollectionAssetGen }

  implicit val arbCatalog: Arbitrary[StacCatalog] = Arbitrary { stacCatalogGen }

  implicit val arbTwoDimBbox: Arbitrary[TwoDimBbox] = Arbitrary {
    twoDimBboxGen
  }

  implicit val arbThreeDimBbox: Arbitrary[ThreeDimBbox] = Arbitrary {
    threeDimBboxGen
  }

  implicit val arbBbox: Arbitrary[Bbox] = Arbitrary {
    bboxGen
  }

  implicit val arbSPDX: Arbitrary[SPDX] = Arbitrary { spdxGen }

  implicit val arbStacLicense: Arbitrary[StacLicense] = Arbitrary { stacLicenseGen }

  implicit val arbStacAssetRole: Arbitrary[StacAssetRole] = Arbitrary {
    assetRoleGen
  }

  implicit val arbStacLink: Arbitrary[StacLink] = Arbitrary {
    stacLinkGen
  }

  implicit val arbLabelClassName: Arbitrary[LabelClassName] = Arbitrary { labelClassNameGen }

  implicit val arbLabelClassClasses: Arbitrary[LabelClassClasses] = Arbitrary { labelClassClassesGen }

  implicit val arbLabelClass: Arbitrary[LabelClass] = Arbitrary { labelClassGen }

  implicit val arbLabelCount: Arbitrary[LabelCount] = Arbitrary { labelCountGen }

  implicit val arbLabelStats: Arbitrary[LabelStats] = Arbitrary { labelStatsGen }

  implicit val arbLabelOverview: Arbitrary[LabelOverview] = Arbitrary { labelOverviewGen }

  implicit val arbLabelTask: Arbitrary[LabelTask] = Arbitrary { labelTaskGen }

  implicit val arbLabelMethod: Arbitrary[LabelMethod] = Arbitrary { labelMethodGen }

  implicit val arbLabelType: Arbitrary[LabelType] = Arbitrary { labelTypeGen }

  implicit val arbLabelProperties: Arbitrary[LabelProperties] = Arbitrary { labelPropertiesGen }

  implicit val arbLabelExtensionProperties: Arbitrary[LabelItemExtension] = Arbitrary {
    labelExtensionPropertiesGen
  }

  implicit val arbLabelLinkExtension: Arbitrary[LabelLinkExtension] = Arbitrary {
    labelLinkExtensionGen
  }

  implicit val arbLayerProperties: Arbitrary[LayerItemExtension] = Arbitrary { layerPropertiesGen }

  implicit val arbEOItemExtension: Arbitrary[EOItemExtension] = Arbitrary {
    eoItemExtensionGen
  }

  implicit val arbBand: Arbitrary[Band] = Arbitrary {
    bandGen
  }

  implicit val arbEOAssetExtension: Arbitrary[EOAssetExtension] = Arbitrary {
    eoAssetExtensionGen
  }

  implicit val arbStacLayerProperties: Arbitrary[StacLayerProperties] = Arbitrary {
    stacLayerPropertiesGen
  }
}

object TestInstances extends TestInstances {}
