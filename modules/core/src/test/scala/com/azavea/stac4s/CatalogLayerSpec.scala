package com.azavea.stac4s

import com.azavea.stac4s.extensions.ItemExtension
import com.azavea.stac4s.extensions.layer._
import cats.data.NonEmptyList
import cats.implicits._
import eu.timepit.refined.types.string.NonEmptyString
import geotrellis.vector.{io => _, _}
import io.circe.syntax._

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CatalogLayerSpec extends AnyFunSpec with Matchers {
  import JsonUtils._

  describe("CatalogLayerSpec") {
    it("Create LC8 Layers Catalog") {
      val root =
        StacCatalog(
          id = "landsat-stac-layers",
          stacVersion = "0.9.0",
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
          href = "../../landsat-8-l1/LC80140332018022LGN00.json",
          rel = StacLinkType.Item,
          _type = None,
          title = None
        ),
        StacLink(
          href = "../../landsat-8-l1/LC80150322018029LGN00.json",
          rel = StacLinkType.Item,
          _type = None,
          title = None
        ),
        StacLink(
          href = "../../landsat-8-l1/LC80150332018029LGN00.json",
          rel = StacLinkType.Item,
          _type = None,
          title = None
        )
      )

      val usLinkItems = paLinkItems ++ List(
        StacLink(
          href = "../../landsat-8-l1/LC81422102018023LGN00.json",
          rel = StacLinkType.Item,
          _type = None,
          title = None
        )
      )

      val layerUS = StacCatalog(
        stacVersion = "0.9.0",
        stacExtensions = List("eo", "view", "https://example.com/stac/landsat-extension/1.0/schema.json"),
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

    it("Create LC8 Layer Item") {
      val collection =
        getJson("/catalogs/landsat-stac-layers/landsat-8-l1/catalog.json").as[StacCollection].valueOr(throw _)
      val layerUS = getJson("/catalogs/landsat-stac-layers/layers/us/catalog.json").as[StacCatalog].valueOr(throw _)

      val item = StacItem(
        id = "LC81422102018023LGN00",
        stacVersion = "0.9.0",
        stacExtensions = List("eo", "view", "layers", "https://example.com/stac/landsat-extension/1.0/schema.json"),
        geometry = """
                     | {
                     |    "type": "Polygon",
                     |    "coordinates": [
                     |      [
                     |        [
                     |          -125.04277,
                     |          38.51378
                     |        ],
                     |        [
                     |          -122.39997,
                     |          38.53006
                     |        ],
                     |        [
                     |          -122.4166,
                     |          36.42097
                     |        ],
                     |        [
                     |          -124.9862,
                     |          36.40587
                     |        ],
                     |        [
                     |          -125.04277,
                     |          38.51378
                     |        ]
                     |      ]
                     |    ]
                     |  }""".stripMargin.parseGeoJson[Polygon],
        bbox = TwoDimBbox(-125.04277, 36.40587, -122.39997, 38.53006),
        properties = Map(
          "collection"               -> "landsat-8-l1".asJson,
          "datetime"                 -> "2018-01-23T06:01:57Z".asJson,
          "eo:sun_azimuth"           -> -70.322.asJson,
          "eo:sun_elevation"         -> -53.777.asJson,
          "eo:cloud_cover"           -> -1.asJson,
          "eo:row"                   -> "210".asJson,
          "eo:col"                   -> "142".asJson,
          "landsat:product_id"       -> "LC08_L1GT_142210_20180123_20180123_01_RT".asJson,
          "landsat:scene_id"         -> "LC81422102018023LGN00".asJson,
          "landsat:processing_level" -> "L1GT".asJson,
          "landsat:tier"             -> "RT".asJson,
          "eo:epsg"                  -> 32610.asJson,
          "eo:instrument"            -> "OLI_TIRS".asJson,
          "eo:off_nadir"             -> 0.asJson,
          "eo:platform"              -> "landsat-8".asJson,
          "eo:gsd"                   -> 15.asJson
        ).asJsonObject.deepMerge(
          LayerItemExtension(NonEmptyList.of(layerUS.id) map { NonEmptyString.unsafeFrom }).asJsonObject
        ), // layer extension
        links = List(
          StacLink(
            href = "./LC81422102018023LGN00.json",
            rel = StacLinkType.Self,
            _type = None,
            title = None
          ),
          StacLink(
            href = "./catalog.json",
            rel = StacLinkType.Parent,
            _type = None,
            title = None
          ),
          StacLink(
            href = "./catalog.json",
            rel = StacLinkType.Collection,
            _type = None,
            title = None
          ),
          StacLink(
            href = "../catalog.json",
            rel = StacLinkType.StacRoot,
            _type = None,
            title = None
          )
        ),
        assets = Map(
          "index" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/142/210/LC08_L1GT_142210_20180123_20180123_01_RT/index.html",
            title = "HTML index page".some,
            description = None,
            roles = Set.empty,
            _type = `text/html`.some
          ),
          "thumbnail" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/142/210/LC08_L1GT_142210_20180123_20180123_01_RT/LC08_L1GT_142210_20180123_20180123_01_RT_thumb_large.jpg",
            title = "Thumbnail image".some,
            description = None,
            roles = Set(StacAssetRole.Thumbnail),
            _type = `image/jpeg`.some
          ),
          "B1" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/142/210/LC08_L1GT_142210_20180123_20180123_01_RT/LC08_L1GT_142210_20180123_20180123_01_RT_B1.TIF",
            title = "Band 1 (coastal)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [0],
          ),
          "B2" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/142/210/LC08_L1GT_142210_20180123_20180123_01_RT/LC08_L1GT_142210_20180123_20180123_01_RT_B2.TIF",
            title = "Band 2 (blue)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [1],
          ),
          "B3" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/142/210/LC08_L1GT_142210_20180123_20180123_01_RT/LC08_L1GT_142210_20180123_20180123_01_RT_B3.TIF",
            title = "Band 3 (green)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [2],
          ),
          "B4" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/142/210/LC08_L1GT_142210_20180123_20180123_01_RT/LC08_L1GT_142210_20180123_20180123_01_RT_B4.TIF",
            title = "Band 4 (red)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [3],
          ),
          "B5" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/142/210/LC08_L1GT_142210_20180123_20180123_01_RT/LC08_L1GT_142210_20180123_20180123_01_RT_B5.TIF",
            title = "Band 5 (nir)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [4],
          ),
          "B6" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/142/210/LC08_L1GT_142210_20180123_20180123_01_RT/LC08_L1GT_142210_20180123_20180123_01_RT_B6.TIF",
            title = "Band 6 (swir16)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [5],
          ),
          "B7" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/142/210/LC08_L1GT_142210_20180123_20180123_01_RT/LC08_L1GT_142210_20180123_20180123_01_RT_B7.TIF",
            title = "Band 7 (swir22)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [6],
          ),
          "B8" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/142/210/LC08_L1GT_142210_20180123_20180123_01_RT/LC08_L1GT_142210_20180123_20180123_01_RT_B8.TIF",
            title = "Band 8 (pan)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [7],
          ),
          "B9" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/142/210/LC08_L1GT_142210_20180123_20180123_01_RT/LC08_L1GT_142210_20180123_20180123_01_RT_B9.TIF",
            title = "Band 9 (cirrus)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [8],
          ),
          "B10" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/142/210/LC08_L1GT_142210_20180123_20180123_01_RT/LC08_L1GT_142210_20180123_20180123_01_RT_B10.TIF",
            title = "Band 10 (lwir)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [9],
          ),
          "B11" -> StacItemAsset(
            href =
              "https://s3-us-west-2.amazonaws.com/landsat-pds/c1/L8/142/210/LC08_L1GT_142210_20180123_20180123_01_RT/LC08_L1GT_142210_20180123_20180123_01_RT_B11.TIF",
            title = "Band 11 (lwir)".some,
            description = None,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [10],
          )
        ),
        collection = collection.id.some
      )

      ItemExtension[LayerItemExtension].getExtensionFields(item) shouldBe LayerItemExtension(
        NonEmptyList.fromListUnsafe(List("layer-us") map { NonEmptyString.unsafeFrom })
      ).valid
      item.asJson.deepDropNullValues shouldBe getJson(
        "/catalogs/landsat-stac-layers/landsat-8-l1/LC81422102018023LGN00.json"
      )
      item.asJson.as[StacItem].valueOr(throw _) shouldBe item
    }
  }
}
