package com.azavea.stac4s

import io.circe.syntax._
import cats.syntax.either._

import geotrellis.vector._

import java.time._

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class CatalogSpec extends AnyFunSpec with Matchers {
  import JsonUtils._

  describe("CatalogSpec") {
    it("Create LC8 Catalog") {
      val root =
        StacCatalog(
          id = "landsat-stac",
          stacVersion = "0.9.0",
          stacExtensions = Nil,
          title = Some("STAC for Landsat data"),
          description = "STAC for Landsat data",
          links = List(
            StacLink(
              href = "https://landsat-stac.s3.amazonaws.com/catalog.json",
              rel = StacLinkType.Self,
              _type = None,
              title = None,
              // should it be an optional thing?
              labelExtAssets = Nil
            ),
            StacLink(
              href = "./catalog.json",
              rel = StacLinkType.StacRoot,
              _type = None,
              title = None,
              // should it be an optional thing?
              labelExtAssets = Nil
            ),
            StacLink(
              href = "landsat-8-l1/catalog.json",
              rel = StacLinkType.Child,
              _type = None,
              title = None,
              // should it be an optional thing?
              labelExtAssets = Nil
            )
          )
        )

      root.asJson.deepDropNullValues shouldBe getJson("/catalogs/landsat8/catalog-v1.json")
      root.asJson.as[StacCatalog].valueOr(throw _) shouldBe root
    }

    it("Create LC8 Collection") {
      val collection = StacCollection(
        stacVersion = "0.9.0",
        stacExtensions = List("eo", "view", "https://example.com/stac/landsat-extension/1.0/schema.json"),
        id = "landsat-8-l1",
        title = Some("Landsat 8 L1"),
        description =
          "Landat 8 imagery radiometrically calibrated and orthorectified using gound points and Digital Elevation Model (DEM) data to correct relief displacement.",
        keywords = List("landsat", "earth observation", "usgs"),
        license = Proprietary(), //SPDX("PDDL-1.0"), // PDDL-1.0
        providers = List(
          StacProvider(
            name = "Development Seed",
            description = None,
            roles = List(Processor),
            url = Some("https://github.com/sat-utils/sat-api")
          )
        ),
        extent = StacExtent(
          spatial = SpatialExtent(List(TwoDimBbox(-180, -90, 180, 90))),
          temporal = Interval(
            List(TemporalExtent(Instant.parse("2013-06-01T00:56:49.001Z"), Instant.parse("2020-01-01T00:56:49.001Z")))
          )
        ),
        // properties can be anything
        // it is a part where extensions can be
        // at least EO, Label and potentially the layer extension
        properties = Map(
          "collection"         -> "landsat-8-l1".asJson,
          "eo:gsd"             -> 15.asJson,
          "eo:platform"        -> "landsat-8".asJson,
          "eo:instrument"      -> "OLI_TIRS".asJson,
          "view:off_nadir"     -> 0.asJson,
          "view:sun_azimuth"   -> 149.01607154.asJson,
          "view:sun_elevation" -> 59.21424700.asJson,
          "view:azimuth"       -> 0.asJson,
          "eo:bands" -> List(
            Map(
              "name"                -> "B1".asJson,
              "common_name"         -> "coastal".asJson,
              "gsd"                 -> 30.asJson,
              "center_wavelength"   -> 0.44.asJson,
              "full_width_half_max" -> 0.02.asJson
            ).asJson,
            Map(
              "name"                -> "B2".asJson,
              "common_name"         -> "blue".asJson,
              "gsd"                 -> 30.asJson,
              "center_wavelength"   -> 0.48.asJson,
              "full_width_half_max" -> 0.06.asJson
            ).asJson,
            Map(
              "name"                -> "B3".asJson,
              "common_name"         -> "green".asJson,
              "gsd"                 -> 30.asJson,
              "center_wavelength"   -> 0.56.asJson,
              "full_width_half_max" -> 0.06.asJson
            ).asJson,
            Map(
              "name"                -> "B4".asJson,
              "common_name"         -> "red".asJson,
              "gsd"                 -> 30.asJson,
              "center_wavelength"   -> 0.65.asJson,
              "full_width_half_max" -> 0.04.asJson
            ).asJson,
            Map(
              "name"                -> "B5".asJson,
              "common_name"         -> "nir".asJson,
              "gsd"                 -> 30.asJson,
              "center_wavelength"   -> 0.86.asJson,
              "full_width_half_max" -> 0.03.asJson
            ).asJson,
            Map(
              "name"                -> "B6".asJson,
              "common_name"         -> "swir16".asJson,
              "gsd"                 -> 30.asJson,
              "center_wavelength"   -> 1.6.asJson,
              "full_width_half_max" -> 0.08.asJson
            ).asJson,
            Map(
              "name"                -> "B7".asJson,
              "common_name"         -> "swir22".asJson,
              "gsd"                 -> 30.asJson,
              "center_wavelength"   -> 2.2.asJson,
              "full_width_half_max" -> 0.22.asJson
            ).asJson,
            Map(
              "name"                -> "B8".asJson,
              "common_name"         -> "pan".asJson,
              "gsd"                 -> 30.asJson,
              "center_wavelength"   -> 0.59.asJson,
              "full_width_half_max" -> 0.18.asJson
            ).asJson,
            Map(
              "name"                -> "B9".asJson,
              "common_name"         -> "cirrus".asJson,
              "gsd"                 -> 30.asJson,
              "center_wavelength"   -> 1.37.asJson,
              "full_width_half_max" -> 0.02.asJson
            ).asJson,
            Map(
              "name"                -> "B10".asJson,
              "common_name"         -> "lwir11".asJson,
              "gsd"                 -> 100.asJson,
              "center_wavelength"   -> 10.9.asJson,
              "full_width_half_max" -> 0.8.asJson
            ).asJson,
            Map(
              "name"                -> "B11".asJson,
              "common_name"         -> "lwir2".asJson,
              "gsd"                 -> 100.asJson,
              "center_wavelength"   -> 12.asJson,
              "full_width_half_max" -> 1.asJson
            ).asJson
          ).asJson
        ).asJsonObject,
        links = List(
          StacLink(
            href = "../../catalog.json",
            rel = StacLinkType.StacRoot,
            _type = None,
            title = None,
            labelExtAssets = Nil
          ),
          StacLink(
            href = "../catalog.json",
            rel = StacLinkType.Parent,
            _type = None,
            title = None,
            labelExtAssets = Nil
          ),
          StacLink(
            href = "./catalog.json",
            rel = StacLinkType.Self,
            _type = None,
            title = None,
            labelExtAssets = Nil
          ),
          StacLink(
            href = "LC81530252014153LGN00.json",
            rel = StacLinkType.Item,
            _type = None,
            title = None,
            labelExtAssets = Nil
          )
        )
      )

      collection.asJson.deepDropNullValues shouldBe getJson("/catalogs/landsat8/collection-v1.json")
      collection.asJson.as[StacCollection].valueOr(throw _) shouldBe collection
    }

    it("Create LC8 Item") {
      val collection = getJson("/catalogs/landsat8/collection-v1.json").as[StacCollection].valueOr(throw _)

      val item = StacItem(
        id = "LC81530252014153LGN00",
        stacVersion = "0.9.0",
        stacExtensions = List("eo", "view", "https://example.com/stac/landsat-extension/1.0/schema.json"),
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
        ).asJsonObject,
        links = List(
          StacLink(
            href = "../../catalog.json",
            rel = StacLinkType.StacRoot,
            _type = None,
            title = None,
            labelExtAssets = Nil
          ),
          StacLink(
            href = "../catalog.json",
            rel = StacLinkType.Parent,
            _type = None,
            title = None,
            labelExtAssets = Nil
          ),
          StacLink(
            href = "LC81530252014153LGN00.json",
            rel = StacLinkType.Self,
            _type = None,
            title = None,
            labelExtAssets = Nil
          ),
          //  { "rel":"alternate", "href": "https://landsatonaws.com/L8/153/025/LC81530252014153LGN00", "type": "text/html"},
          StacLink(
            href = "https://landsatonaws.com/L8/153/025/LC81530252014153LGN0",
            rel = StacLinkType.Alternate,
            _type = Some(`text/html`),
            title = None,
            labelExtAssets = Nil
          )
        ),
        assets = Map(
          "thumbnail" -> StacItemAsset(
            href =
              "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_thumb_large.jpg",
            title = Some("Thumbnail"),
            description = Some("A medium sized thumbnail"),
            roles = List(StacAssetRole.Thumbnail),
            _type = Some(`image/jpeg`)
          ),
          "metadata" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_MTL.txt",
            title = Some("Original Metadata"),
            description = Some("The original MTL metadata file provided for each Landsat scene"),
            roles = List(StacAssetRole.Metadata),
            _type = Some(VendorMediaType("mtl"))
          ),
          "B1" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B1.TIF",
            title = Some("Coastal Band (B1)"),
            description = Some("Coastal Band Top Of the Atmosphere"),
            roles = Nil,
            _type = Some(`image/tiff`)
            // "eo:bands": [0],
          ),
          "B2" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B2.TIF",
            title = Some("Blue Band (B2)"),
            description = Some("Blue Band Top Of the Atmosphere"),
            roles = Nil,
            _type = Some(`image/tiff`)
            // "eo:bands": [1],
          ),
          "B3" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B3.TIF",
            title = Some("Green Band (B3)"),
            description = Some("Green Band Top Of the Atmosphere"),
            roles = Nil,
            _type = Some(`image/tiff`)
            // "eo:bands": [2],
          ),
          "B4" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B4.TIF",
            title = Some("Red Band (B4)"),
            description = Some("Red Band Top Of the Atmosphere"),
            roles = Nil,
            _type = Some(`image/tiff`)
            // "eo:bands": [3],
          ),
          "B5" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B5.TIF",
            title = Some("NIR Band (B5)"),
            description = Some("NIR Band Top Of the Atmosphere"),
            roles = Nil,
            _type = Some(`image/tiff`)
            // "eo:bands": [4],
          ),
          "B6" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B6.TIF",
            title = Some("SWIR (B6)"),
            description = Some("SWIR Band Top Of the Atmosphere"),
            roles = Nil,
            _type = Some(`image/tiff`)
            // "eo:bands": [5],
          ),
          "B7" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B7.TIF",
            title = Some("SWIR Band (B7)"),
            description = Some("SWIR Band Top Of the Atmosphere"),
            roles = Nil,
            _type = Some(`image/tiff`)
            // "eo:bands": [6],
          ),
          "B8" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B8.TIF",
            title = Some("Panchromatic Band (B8)"),
            description = Some("Panchromatic Band Top Of the Atmosphere"),
            roles = Nil,
            _type = Some(`image/tiff`)
            // "eo:bands": [7],
          ),
          "B9" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B9.TIF",
            title = Some("Cirrus Band (B9)"),
            description = Some("Cirrus Band Top Of the Atmosphere"),
            roles = Nil,
            _type = Some(`image/tiff`)
            // "eo:bands": [8],
          ),
          "B10" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B10.TIF",
            title = Some("LWIR Band (B10)"),
            description = Some("LWIR Band Top Of the Atmosphere"),
            roles = Nil,
            _type = Some(`image/tiff`)
            // "eo:bands": [9],
          ),
          "B11" -> StacItemAsset(
            href = "http://landsat-pds.s3.amazonaws.com/L8/153/025/LC81530252014153LGN00/LC81530252014153LGN00_B11.TIF",
            title = Some("LWIR Band (B11)"),
            description = Some("LWIR Band Top Of the Atmosphere"),
            roles = Nil,
            _type = Some(`image/tiff`)
            // "eo:bands": [10],
          )
        ),
        collection = Some(collection.id)
      )

      item.asJson.deepDropNullValues shouldBe getJson("/catalogs/landsat8/item-v1.json")
      item.asJson.as[StacItem].valueOr(throw _) shouldBe item
    }
  }
}
