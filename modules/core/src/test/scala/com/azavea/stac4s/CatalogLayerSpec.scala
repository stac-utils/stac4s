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
              href = "./layers/ca/catalog.json",
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
      val layerUS = StacCatalog(
        stacVersion = "0.9.0",
        stacExtensions = List("eo", "view", "https://example.com/stac/landsat-extension/1.0/schema.json"),
        id = "layer-us",
        title = "Landsat 8 L1".some,
        description = "US STAC Layer",
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
            href = "../../landsat-8-l1/2014-153/LC81530252014153LGN00.json",
            rel = StacLinkType.Item,
            _type = None,
            title = None
          )
        )
      )

      val layerCA = layerUS.copy(
        id = "layer-ca",
        description = "CA STAC Layer"
      )

      layerUS.asJson.deepDropNullValues shouldBe getJson("/catalogs/landsat-stac-layers/layers/us/catalog.json")
      layerUS.asJson.as[StacCatalog].valueOr(throw _) shouldBe layerUS

      layerCA.asJson.deepDropNullValues shouldBe getJson("/catalogs/landsat-stac-layers/layers/ca/catalog.json")
      layerCA.asJson.as[StacCatalog].valueOr(throw _) shouldBe layerCA
    }

    it("Create LC8 Layer Item") {
      val collection =
        getJson("/catalogs/landsat-stac-layers/landsat-8-l1/catalog.json").as[StacCollection].valueOr(throw _)
      val layerUS = getJson("/catalogs/landsat-stac-layers/layers/us/catalog.json").as[StacCatalog].valueOr(throw _)
      val layerCA = getJson("/catalogs/landsat-stac-layers/layers/ca/catalog.json").as[StacCatalog].valueOr(throw _)

      val item = StacItem(
        id = "LC81530252014153LGN00",
        stacVersion = "0.9.0",
        stacExtensions = List("eo", "view", "layers", "https://example.com/stac/landsat-extension/1.0/schema.json"),
        geometry = """
                     |{
                     |    "type": "Polygon",
                     |    "coordinates": [
                     |      [
                     |        [
                     |          51.33855,
                     |          72.27502
                     |        ],
                     |        [
                     |          51.36812,
                     |          75.70821
                     |        ],
                     |        [
                     |          49.19092,
                     |          75.67662
                     |        ],
                     |        [
                     |          49.16354,
                     |          72.39640
                     |        ],
                     |        [
                     |          51.33855,
                     |          72.27502
                     |        ]
                     |      ]
                     |    ]
                     |  }""".stripMargin.parseGeoJson[Polygon],
        bbox = TwoDimBbox(49.16354, 72.27502, 51.36812, 75.67662),
        properties = Map(
          "collection"                           -> "landsat-8-l1".asJson,
          "datetime"                             -> "2014-06-02T09:22:02Z".asJson,
          "eo:gsd"                               -> 15.asJson,
          "eo:cloud_cover"                       -> 10.asJson,
          "view:off_nadir"                       -> 0.asJson,
          "view:sun_azimuth"                     -> 149.01607154.asJson,
          "view:sun_elevation"                   -> 59.21424700.asJson,
          "view:azimuth"                         -> 0.asJson,
          "landsat:wrs_path"                     -> 153.asJson,
          "landsat:wrs_row"                      -> 25.asJson,
          "landsat:earth_sun_distance"           -> 1.0141560.asJson,
          "landsat:ground_control_points_verify" -> 114.asJson,
          "landsat:geometric_rmse_model"         -> 7.562.asJson,
          "landsat:image_quality_tirs"           -> 9.asJson,
          "landsat:ground_control_points_model"  -> 313.asJson,
          "landsat:geometric_rmse_model_x"       -> 5.96.asJson,
          "landsat:geometric_rmse_model_y"       -> 4.654.asJson,
          "landsat:geometric_rmse_verify"        -> 5.364.asJson,
          "landsat:image_quality_oli"            -> 9.asJson
        ).asJsonObject.deepMerge(
          LayerItemExtension(NonEmptyList.of(layerUS.id, layerCA.id) map { NonEmptyString.unsafeFrom }).asJsonObject
        ), // layer extension
        links = List(
          StacLink(
            href = "../../catalog.json",
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
            href = "./LC81530252014153LGN00.json",
            rel = StacLinkType.Self,
            _type = None,
            title = None
          ),
          //  { "rel":"alternate", "href": "https://landsatonaws.com/L8/153/025/LC81530252014153LGN00", "type": "text/html"},
          StacLink(
            href = "https://landsatonaws.com/L8/153/025/LC81530252014153LGN0",
            rel = StacLinkType.Alternate,
            _type = `text/html`.some,
            title = None
          )
        ),
        assets = Map(
          "thumbnail" -> StacItemAsset(
            href =
              "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_thumb_large.jpg",
            title = "Thumbnail".some,
            description = "A medium sized thumbnail".some,
            roles = Set(StacAssetRole.Thumbnail),
            _type = `image/jpeg`.some
          ),
          "metadata" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_MTL.txt",
            title = "Original Metadata".some,
            description = "The original MTL metadata file provided for each Landsat scene".some,
            roles = Set(StacAssetRole.Metadata),
            _type = VendorMediaType("mtl").some
          ),
          "B1" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B1.TIF",
            title = "Coastal Band (B1)".some,
            description = "Coastal Band Top Of the Atmosphere".some,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [0],
          ),
          "B2" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B2.TIF",
            title = "Blue Band (B2)".some,
            description = "Blue Band Top Of the Atmosphere".some,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [1],
          ),
          "B3" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B3.TIF",
            title = "Green Band (B3)".some,
            description = "Green Band Top Of the Atmosphere".some,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [2],
          ),
          "B4" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B4.TIF",
            title = "Red Band (B4)".some,
            description = "Red Band Top Of the Atmosphere".some,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [3],
          ),
          "B5" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B5.TIF",
            title = "NIR Band (B5)".some,
            description = "NIR Band Top Of the Atmosphere".some,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [4],
          ),
          "B6" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B6.TIF",
            title = "SWIR (B6)".some,
            description = "SWIR Band Top Of the Atmosphere".some,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [5],
          ),
          "B7" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B7.TIF",
            title = "SWIR Band (B7)".some,
            description = "SWIR Band Top Of the Atmosphere".some,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [6],
          ),
          "B8" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B8.TIF",
            title = "Panchromatic Band (B8)".some,
            description = "Panchromatic Band Top Of the Atmosphere".some,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [7],
          ),
          "B9" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B9.TIF",
            title = "Cirrus Band (B9)".some,
            description = "Cirrus Band Top Of the Atmosphere".some,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [8],
          ),
          "B10" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B10.TIF",
            title = "LWIR Band (B10)".some,
            description = "LWIR Band Top Of the Atmosphere".some,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [9],
          ),
          "B11" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B11.TIF",
            title = "LWIR Band (B11)".some,
            description = "LWIR Band Top Of the Atmosphere".some,
            roles = Set.empty,
            _type = `image/tiff`.some
            // "eo:bands": [10],
          )
        ),
        collection = collection.id.some
      )

      ItemExtension[LayerItemExtension].getExtensionFields(item) shouldBe LayerItemExtension(
        NonEmptyList.fromListUnsafe(List("layer-us", "layer-ca") map { NonEmptyString.unsafeFrom })
      ).valid
      item.asJson.deepDropNullValues shouldBe getJson(
        "/catalogs/landsat-stac-layers/landsat-8-l1/2014-153/LC81530252014153LGN00.json"
      )
      item.asJson.as[StacItem].valueOr(throw _) shouldBe item
    }
  }
}
