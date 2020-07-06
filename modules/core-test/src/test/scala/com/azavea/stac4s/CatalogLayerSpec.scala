package com.azavea.stac4s

import com.azavea.stac4s.extensions.eo._

import com.azavea.stac4s.extensions.ItemExtension
import com.azavea.stac4s.extensions.layer._
import cats.data.NonEmptyList
import cats.implicits._
import eu.timepit.refined.types.numeric.PosDouble
import eu.timepit.refined.types.string.NonEmptyString
import geotrellis.vector.{io => _, _}
import io.circe.syntax._

import java.time.Instant

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CatalogLayerSpec extends AnyFunSpec with Matchers {
  import JsonUtils._

  describe("CatalogLayerSpec") {
    it("Create LC8 Layers Catalog") {
      val root =
        StacCatalog(
          id = "landsat-stac-layers",
          stacVersion = "1.0.0-beta.1",
          stacExtensions = Nil,
          title = "STAC for Landsat data".some,
          description = "STAC for Landsat data",
          links = List(
            StacLink(
              href = "./catalog.json",
              rel = StacLinkType.Self,
              _type = None,
              title = None
            ),
            StacLink(
              href = "./catalog.json",
              rel = StacLinkType.StacRoot,
              _type = None,
              title = None
            ),
            StacLink(
              href = "./landsat-8-l1/catalog.json",
              rel = StacLinkType.Child,
              _type = None,
              title = None
            ),
            StacLink(
              href = "./layers/pa/catalog.json",
              rel = StacLinkType.Child,
              _type = None,
              title = None
            ),
            StacLink(
              href = "./layers/us/catalog.json",
              rel = StacLinkType.Child,
              _type = None,
              title = None
            )
          )
        )

      root.asJson.deepDropNullValues shouldBe getJson("/catalogs/landsat-stac-layers/catalog.json")
      root.asJson.as[StacCatalog].valueOr(throw _) shouldBe root
    }

    it("Create LC8 Layer Catalog") {

      val baseLinkItems = List(
        StacLink(
          href = "../../catalog.json",
          rel = StacLinkType.StacRoot,
          _type = None,
          title = None
        ),
        StacLink(
          href = "../../catalog.json",
          rel = StacLinkType.Parent,
          _type = None,
          title = None
        ),
        StacLink(
          href = "./catalog.json",
          rel = StacLinkType.Self,
          _type = None,
          title = None
        )
      )

      val paLinkItems = List(
        StacLink(
          href = "../../landsat-8-l1/2018-06/LC80140332018166LGN00.json",
          rel = StacLinkType.Item,
          _type = None,
          title = None
        ),
        StacLink(
          href = "../../landsat-8-l1/2018-05/LC80150322018141LGN00.json",
          rel = StacLinkType.Item,
          _type = None,
          title = None
        ),
        StacLink(
          href = "../../landsat-8-l1/2018-07/LC80150332018189LGN00.json",
          rel = StacLinkType.Item,
          _type = None,
          title = None
        )
      )

      val usLinkItems = paLinkItems ++ List(
        StacLink(
          href = "../../landsat-8-l1/2018-06/LC80300332018166LGN00.json",
          rel = StacLinkType.Item,
          _type = None,
          title = None
        )
      )

      val layerUS = StacCatalog(
        stacVersion = "1.0.0-beta.1",
        stacExtensions = Nil,
        id = "layer-us",
        title = "Landsat 8 L1".some,
        description = "US STAC Layer",
        links = baseLinkItems ++ usLinkItems
      )

      val layerPA = layerUS.copy(
        id = "layer-pa",
        description = "PA STAC Layer",
        links = baseLinkItems ++ paLinkItems
      )

      layerUS.asJson.deepDropNullValues shouldBe getJson("/catalogs/landsat-stac-layers/layers/us/catalog.json")
      layerUS.asJson.as[StacCatalog].valueOr(throw _) shouldBe layerUS

      layerPA.asJson.deepDropNullValues shouldBe getJson("/catalogs/landsat-stac-layers/layers/pa/catalog.json")
      layerPA.asJson.as[StacCatalog].valueOr(throw _) shouldBe layerPA
    }

    it("Create LC8 Collection") {
      val collection = StacCollection(
        stacVersion = "1.0.0-beta.1",
        stacExtensions = List("eo", "view", "https://example.com/stac/landsat-extension/1.0/schema.json"),
        id = "landsat-8-l1",
        title = "Landsat 8 L1".some,
        description =
          "Landat 8 imagery radiometrically calibrated and orthorectified using gound points and Digital Elevation Model (DEM) data to correct relief displacement.",
        keywords = List("landsat", "earth observation", "usgs"),
        license = Proprietary(), //SPDX("PDDL-1.0"), // PDDL-1.0
        providers = List(
          StacProvider(
            name = "Development Seed",
            description = None,
            roles = List(Processor),
            url = "https://github.com/sat-utils/sat-api".some
          )
        ),
        extent = StacExtent(
          spatial = SpatialExtent(List(TwoDimBbox(-180, -90, 180, 90))),
          temporal = Interval(
            List(TemporalExtent(Instant.parse("2018-05-01T00:00:00Z"), Instant.parse("2018-08-01T00:00:00Z")))
          )
        ),
        summaries = ().asJsonObject,
        // properties can be anything
        // it is a part where extensions can be
        // at least EO, Label and potentially the layer extension
        properties = Map(
          "collection"         -> "landsat-8-l1".asJson,
          "gsd"                -> 15.asJson,
          "platform"           -> "landsat-8".asJson,
          "instruments"        -> List("OLI_TIRS").asJson,
          "view:off_nadir"     -> 0.asJson,
          "view:sun_azimuth"   -> 149.01607154.asJson,
          "view:sun_elevation" -> 59.21424700.asJson,
          "view:azimuth"       -> 0.asJson
        ).asJsonObject.deepMerge(
          EOAssetExtension(
            NonEmptyList(
              Band(
                name = NonEmptyString.unsafeFrom("B1"),
                commonName = NonEmptyString.unsafeFrom("coastal").some,
                description = None,
                centerWavelength = PosDouble.unsafeFrom(0.44).some,
                fullWidthHalfMax = PosDouble.unsafeFrom(0.02).some
              ),
              List(
                Band(
                  name = NonEmptyString.unsafeFrom("B2"),
                  commonName = NonEmptyString.unsafeFrom("blue").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(0.48).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.06).some
                ),
                Band(
                  name = NonEmptyString.unsafeFrom("B3"),
                  commonName = NonEmptyString.unsafeFrom("green").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(0.56).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.06).some
                ),
                Band(
                  name = NonEmptyString.unsafeFrom("B4"),
                  commonName = NonEmptyString.unsafeFrom("red").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(0.65).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.04).some
                ),
                Band(
                  name = NonEmptyString.unsafeFrom("B5"),
                  commonName = NonEmptyString.unsafeFrom("nir").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(0.86).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.03).some
                ),
                Band(
                  name = NonEmptyString.unsafeFrom("B6"),
                  commonName = NonEmptyString.unsafeFrom("swir16").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(1.6).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.08).some
                ),
                Band(
                  name = NonEmptyString.unsafeFrom("B7"),
                  commonName = NonEmptyString.unsafeFrom("swir22").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(2.2).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.22).some
                ),
                Band(
                  name = NonEmptyString.unsafeFrom("B8"),
                  commonName = NonEmptyString.unsafeFrom("pan").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(0.59).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.18).some
                ),
                Band(
                  name = NonEmptyString.unsafeFrom("B9"),
                  commonName = NonEmptyString.unsafeFrom("cirrus").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(1.37).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.02).some
                ),
                Band(
                  name = NonEmptyString.unsafeFrom("B10"),
                  commonName = NonEmptyString.unsafeFrom("lwir11").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(10.9).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.8).some
                ),
                Band(
                  name = NonEmptyString.unsafeFrom("B11"),
                  commonName = NonEmptyString.unsafeFrom("lwir2").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(12.0).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(1.0).some
                )
              )
            )
          ).asJsonObject
        ),
        links = List(
          StacLink(
            href = "../catalog.json",
            rel = StacLinkType.StacRoot,
            _type = None,
            title = None
          ),
          StacLink(
            href = "../catalog.json",
            rel = StacLinkType.Parent,
            _type = None,
            title = None
          ),
          StacLink(
            href = "./catalog.json",
            rel = StacLinkType.Self,
            _type = None,
            title = None
          ),
          StacLink(
            href = "./2018-06/LC80140332018166LGN00.json",
            rel = StacLinkType.Item,
            _type = None,
            title = None
          ),
          StacLink(
            href = "./2018-05/LC80150322018141LGN00.json",
            rel = StacLinkType.Item,
            _type = None,
            title = None
          ),
          StacLink(
            href = "./2018-07/LC80150332018189LGN00.json",
            rel = StacLinkType.Item,
            _type = None,
            title = None
          ),
          StacLink(
            href = "./2018-06/LC80300332018166LGN00.json",
            rel = StacLinkType.Item,
            _type = None,
            title = None
          )
        )
      )

      collection.asJson.deepDropNullValues shouldBe getJson("/catalogs/landsat-stac-layers/landsat-8-l1/catalog.json")
      collection.asJson.as[StacCollection].valueOr(throw _) shouldBe collection
    }

    it("Create LC8 Layer Item") {
      val collection =
        getJson("/catalogs/landsat-stac-layers/landsat-8-l1/catalog.json").as[StacCollection].valueOr(throw _)
      val layerUS = getJson("/catalogs/landsat-stac-layers/layers/us/catalog.json").as[StacCatalog].valueOr(throw _)

      val item = StacItem(
        id = "LC80300332018166LGN00",
        stacVersion = "1.0.0-beta.1",
        stacExtensions =
          List("eo", "view", "proj", "layer", "https://example.com/stac/landsat-extension/1.0/schema.json"),
        geometry = """
                     | {
                     |    "type": "Polygon",
                     |    "coordinates": [
                     |      [
                     |        [
                     |          -100.84368079413701,
                     |          39.97210491033466
                     |        ],
                     |        [
                     |          -98.67492641719046,
                     |          39.54833037653145
                     |        ],
                     |        [
                     |          -99.23946071016417,
                     |          37.81370881408165
                     |        ],
                     |        [
                     |          -101.40560438472555,
                     |          38.24476872678675
                     |        ],
                     |        [
                     |          -100.84368079413701,
                     |          39.97210491033466
                     |        ]
                     |      ]
                     |    ]
                     |  }""".stripMargin.parseGeoJson[Polygon],
        bbox = TwoDimBbox(-101.40793, 37.81084, -98.6721, 39.97469),
        properties = Map(
          "collection"               -> "landsat-8-l1".asJson,
          "datetime"                 -> "2018-06-15T17:18:03Z".asJson,
          "view:sun_azimuth"         -> 125.5799919.asJson,
          "view:sun_elevation"       -> 66.54407242.asJson,
          "eo:cloud_cover"           -> 0.asJson,
          "landsat:row"              -> "033".asJson,
          "landsat:column"           -> "030".asJson,
          "landsat:product_id"       -> "LC08_L1TP_030033_20180615_20180703_01_T1".asJson,
          "landsat:scene_id"         -> "LC80300332018166LGN00".asJson,
          "landsat:processing_level" -> "L1TP".asJson,
          "landsat:tier"             -> "T1".asJson,
          "proj:epsg"                -> 32614.asJson,
          "instruments"              -> List("OLI_TIRS").asJson,
          "view:off_nadir"           -> 0.asJson,
          "platform"                 -> "landsat-8".asJson,
          "gsd"                      -> 15.asJson
        ).asJsonObject.deepMerge(
          LayerItemExtension(NonEmptyList.of(NonEmptyString.unsafeFrom(layerUS.id))).asJsonObject
        ), // layer extension
        links = List(
          StacLink(
            href = "./LC80300332018166LGN00.json",
            rel = StacLinkType.Self,
            _type = None,
            title = None
          ),
          StacLink(
            href = "../catalog.json",
            rel = StacLinkType.Parent,
            _type = None,
            title = None
          ),
          StacLink(
            href = "../catalog.json",
            rel = StacLinkType.Collection,
            _type = None,
            title = None
          ),
          StacLink(
            href = "../../catalog.json",
            rel = StacLinkType.StacRoot,
            _type = None,
            title = None
          )
        ),
        assets = Map(
          "index" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/033/LC08_L1TP_030033_20180615_20180703_01_T1/index.html",
            title = "HTML index page".some,
            description = None,
            roles = Set.empty,
            _type = `text/html`.some
          ),
          "thumbnail" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/033/LC08_L1TP_030033_20180615_20180703_01_T1/LC08_L1TP_030033_20180615_20180703_01_T1_thumb_large.jpg",
            title = "Thumbnail image".some,
            description = None,
            roles = Set(StacAssetRole.Thumbnail),
            _type = `image/jpeg`.some
          ),
          "B1" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/033/LC08_L1TP_030033_20180615_20180703_01_T1/LC08_L1TP_030033_20180615_20180703_01_T1_B1.TIF",
            title = "Band 1 (coastal)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some,
            EOAssetExtension(
              NonEmptyList(
                Band(
                  name = NonEmptyString.unsafeFrom("B1"),
                  commonName = NonEmptyString.unsafeFrom("coastal").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(0.44).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.02).some
                ),
                Nil
              )
            ).asJsonObject
          ),
          "B2" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/033/LC08_L1TP_030033_20180615_20180703_01_T1/LC08_L1TP_030033_20180615_20180703_01_T1_B2.TIF",
            title = "Band 2 (blue)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some,
            EOAssetExtension(
              NonEmptyList(
                Band(
                  name = NonEmptyString.unsafeFrom("B2"),
                  commonName = NonEmptyString.unsafeFrom("blue").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(0.48).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.06).some
                ),
                Nil
              )
            ).asJsonObject
          ),
          "B3" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/033/LC08_L1TP_030033_20180615_20180703_01_T1/LC08_L1TP_030033_20180615_20180703_01_T1_B3.TIF",
            title = "Band 3 (green)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some,
            EOAssetExtension(
              NonEmptyList(
                Band(
                  name = NonEmptyString.unsafeFrom("B3"),
                  commonName = NonEmptyString.unsafeFrom("green").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(0.56).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.06).some
                ),
                Nil
              )
            ).asJsonObject
          ),
          "B4" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/033/LC08_L1TP_030033_20180615_20180703_01_T1/LC08_L1TP_030033_20180615_20180703_01_T1_B4.TIF",
            title = "Band 4 (red)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some,
            EOAssetExtension(
              NonEmptyList(
                Band(
                  name = NonEmptyString.unsafeFrom("B4"),
                  commonName = NonEmptyString.unsafeFrom("red").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(0.65).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.04).some
                ),
                Nil
              )
            ).asJsonObject
          ),
          "B5" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/033/LC08_L1TP_030033_20180615_20180703_01_T1/LC08_L1TP_030033_20180615_20180703_01_T1_B5.TIF",
            title = "Band 5 (nir)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some,
            EOAssetExtension(
              NonEmptyList(
                Band(
                  name = NonEmptyString.unsafeFrom("B5"),
                  commonName = NonEmptyString.unsafeFrom("nir").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(0.86).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.03).some
                ),
                Nil
              )
            ).asJsonObject
          ),
          "B6" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/033/LC08_L1TP_030033_20180615_20180703_01_T1/LC08_L1TP_030033_20180615_20180703_01_T1_B6.TIF",
            title = "Band 6 (swir16)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some,
            EOAssetExtension(
              NonEmptyList(
                Band(
                  name = NonEmptyString.unsafeFrom("B6"),
                  commonName = NonEmptyString.unsafeFrom("swir16").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(1.6).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.08).some
                ),
                Nil
              )
            ).asJsonObject
          ),
          "B7" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/033/LC08_L1TP_030033_20180615_20180703_01_T1/LC08_L1TP_030033_20180615_20180703_01_T1_B7.TIF",
            title = "Band 7 (swir22)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some,
            EOAssetExtension(
              NonEmptyList(
                Band(
                  name = NonEmptyString.unsafeFrom("B7"),
                  commonName = NonEmptyString.unsafeFrom("swir22").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(2.2).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.22).some
                ),
                Nil
              )
            ).asJsonObject
          ),
          "B8" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/033/LC08_L1TP_030033_20180615_20180703_01_T1/LC08_L1TP_030033_20180615_20180703_01_T1_B8.TIF",
            title = "Band 8 (pan)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some,
            EOAssetExtension(
              NonEmptyList(
                Band(
                  name = NonEmptyString.unsafeFrom("B8"),
                  commonName = NonEmptyString.unsafeFrom("pan").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(0.59).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.18).some
                ),
                Nil
              )
            ).asJsonObject
          ),
          "B9" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/033/LC08_L1TP_030033_20180615_20180703_01_T1/LC08_L1TP_030033_20180615_20180703_01_T1_B9.TIF",
            title = "Band 9 (cirrus)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some,
            EOAssetExtension(
              NonEmptyList(
                Band(
                  name = NonEmptyString.unsafeFrom("B9"),
                  commonName = NonEmptyString.unsafeFrom("cirrus").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(1.37).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.02).some
                ),
                Nil
              )
            ).asJsonObject
          ),
          "B10" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/033/LC08_L1TP_030033_20180615_20180703_01_T1/LC08_L1TP_030033_20180615_20180703_01_T1_B10.TIF",
            title = "Band 10 (lwir)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some,
            EOAssetExtension(
              NonEmptyList(
                Band(
                  name = NonEmptyString.unsafeFrom("B10"),
                  commonName = NonEmptyString.unsafeFrom("lwir11").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(10.9).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(0.8).some
                ),
                Nil
              )
            ).asJsonObject
          ),
          "B11" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/033/LC08_L1TP_030033_20180615_20180703_01_T1/LC08_L1TP_030033_20180615_20180703_01_T1_B11.TIF",
            title = "Band 11 (lwir)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some,
            EOAssetExtension(
              NonEmptyList(
                Band(
                  name = NonEmptyString.unsafeFrom("B11"),
                  commonName = NonEmptyString.unsafeFrom("lwir2").some,
                  description = None,
                  centerWavelength = PosDouble.unsafeFrom(12.0).some,
                  fullWidthHalfMax = PosDouble.unsafeFrom(1.0).some
                ),
                Nil
              )
            ).asJsonObject
          ),
          "ANG" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/033/LC08_L1TP_030033_20180615_20180703_01_T1/LC08_L1TP_030033_20180615_20180703_01_T1_ANG.txt",
            title = "Angle coefficients file".some,
            description = None,
            roles = Set.empty,
            _type = `text/plain`.some
          ),
          "MTL" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/033/LC08_L1TP_030033_20180615_20180703_01_T1/LC08_L1TP_030033_20180615_20180703_01_T1_MTL.txt",
            title = "original metadata file".some,
            description = None,
            roles = Set.empty,
            _type = `text/plain`.some
          ),
          "BQA" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/030/033/LC08_L1TP_030033_20180615_20180703_01_T1/LC08_L1TP_030033_20180615_20180703_01_T1_BQA.TIF",
            title = "Band quality data".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some
          )
        ),
        collection = collection.id.some
      )

      ItemExtension[LayerItemExtension].getExtensionFields(item) shouldBe LayerItemExtension(
        NonEmptyList.fromListUnsafe(List("layer-us") map { NonEmptyString.unsafeFrom })
      ).valid
      item.asJson.deepDropNullValues shouldBe getJson(
        "/catalogs/landsat-stac-layers/landsat-8-l1/2018-06/LC80300332018166LGN00.json"
      )
      item.asJson.as[StacItem].valueOr(throw _) shouldBe item
    }
  }
}
