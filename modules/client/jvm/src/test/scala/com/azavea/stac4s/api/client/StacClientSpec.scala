package com.azavea.stac4s.api.client

import cats.effect.{Blocker, IO}
import cats.syntax.either._
import com.azavea.IOSpec
import eu.timepit.refined.collection.Empty
import eu.timepit.refined.types.all.NonEmptyString
import io.circe.parser._
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client3.http4s.Http4sBackend
import sttp.client3.impl.cats.CatsMonadAsyncError
import sttp.client3.testing.SttpBackendStub
import sttp.client3.{Response, UriContext}
import sttp.model.StatusCode

class StacClientSpec extends IOSpec {

  lazy val backend: SttpBackendStub[IO, Nothing] =
    SttpBackendStub(new CatsMonadAsyncError[IO]())
      .whenRequestMatches(_.uri.path == Seq("collections"))
      .thenRespondF { _ =>
        IO(
          Response(
            Right(parse("""
            |{
            |   "collections":[
            |      {
            |         "stac_version":"1.0.0-beta.2",
            |         "stac_extensions":[
            |
            |         ],
            |         "id":"aviris_2006",
            |         "title":null,
            |         "description":"aviris_2006",
            |         "keywords":[
            |
            |         ],
            |         "license":"proprietary",
            |         "providers":[
            |
            |         ],
            |         "extent":{
            |            "spatial":{
            |               "bbox":[
            |                  [
            |                     -122.857491,
            |                     32.093266,
            |                     -76.55229,
            |                     48.142484
            |                  ]
            |               ]
            |            },
            |            "temporal":{
            |               "interval":[
            |                  [
            |                     "2006-04-26T17:52:00Z",
            |                     "2006-11-15T19:42:00Z"
            |                  ]
            |               ]
            |            }
            |         },
            |         "summaries":{
            |
            |         },
            |         "properties":{
            |
            |         },
            |         "links":[
            |            {
            |               "href":"http://localhost:9090/collections/aviris_2006/items",
            |               "rel":"items",
            |               "type":"application/json",
            |               "title":null
            |            },
            |            {
            |               "href":"http://localhost:9090/collections/aviris_2006",
            |               "rel":"self",
            |               "type":"application/json",
            |               "title":null
            |            }
            |         ]
            |      }
            |   ]
            |}
            |""".stripMargin).valueOr(throw _)),
            StatusCode.Ok,
            ""
          )
        )
      }
      .whenRequestMatches(_.uri.path == Seq("collections", "aviris_2006", "items"))
      .thenRespondF { _ =>
        IO(
          Response(
            Right(parse("""
                          |{
                          |   "type":"FeatureCollection",
                          |   "features":[
                          |      {
                          |         "id":"aviris_f060426t01p00r03_sc01",
                          |         "stac_version":"1.0.0-beta.2",
                          |         "stac_extensions":[
                          |
                          |         ],
                          |         "type":"Feature",
                          |         "geometry":{
                          |            "type":"Polygon",
                          |            "coordinates":[
                          |               [
                          |                  [
                          |                     -107.771817,
                          |                     37.913396
                          |                  ],
                          |                  [
                          |                     -107.739984,
                          |                     37.914142
                          |                  ],
                          |                  [
                          |                     -107.744691,
                          |                     38.040563
                          |                  ],
                          |                  [
                          |                     -107.776579,
                          |                     38.039814
                          |                  ],
                          |                  [
                          |                     -107.771817,
                          |                     37.913396
                          |                  ]
                          |               ]
                          |            ]
                          |         },
                          |         "bbox":[
                          |            -107.776579,
                          |            37.913396,
                          |            -107.739984,
                          |            38.040563
                          |         ],
                          |         "links":[
                          |            {
                          |               "href":"http://localhost:9090/collections/aviris_2006_60426",
                          |               "rel":"collection",
                          |               "type":"application/json",
                          |               "title":null
                          |            },
                          |            {
                          |               "href":"http://localhost:9090/collections/aviris_2006_60426/items/aviris_f060426t01p00r03_sc01",
                          |               "rel":"self",
                          |               "type":"application/json",
                          |               "title":null
                          |            }
                          |         ],
                          |         "assets":{
                          |            "ftp":{
                          |               "href":"ftp://avoil:Gulf0il$pill@popo.jpl.nasa.gov/y06_data/f060426t01p00r03.tar.gz",
                          |               "title":"ftp",
                          |               "description":"AVIRIS data archive. The file size is described by the 'Gzip File Size' property.",
                          |               "roles":[
                          |
                          |               ],
                          |               "type":"application/gzip"
                          |            },
                          |            "rgb":{
                          |               "href":"http://aviris.jpl.nasa.gov/aviris_locator/y06_RGB/f060426t01p00r03_sc01_RGB.jpeg",
                          |               "title":"rgb",
                          |               "description":"Full resolution RGB image captured by the flight",
                          |               "roles":[
                          |
                          |               ],
                          |               "type":"image/jpeg"
                          |            },
                          |            "kml_overlay":{
                          |               "href":"http://aviris.jpl.nasa.gov/aviris_locator/y06_KML/f060426t01p00r03_sc01_overlay_KML.kml",
                          |               "title":"kml_overlay",
                          |               "description":"KML file describing the bounding box of the flight",
                          |               "roles":[
                          |
                          |               ],
                          |               "type":"application/vnd.google-earth.kml+xml"
                          |            },
                          |            "rgb_small":{
                          |               "href":"http://aviris.jpl.nasa.gov/aviris_locator/y06_RGB/f060426t01p00r03_sc01_RGB-W200.jpg",
                          |               "title":"rgb_small",
                          |               "description":"A lower resolution thumbnail of the same image as the 'rgb' asset.",
                          |               "roles":[
                          |
                          |               ],
                          |               "type":"image/jpeg"
                          |            },
                          |            "flight_log":{
                          |               "href":"http://aviris.jpl.nasa.gov/cgi/flights_06.cgi?step=view_flightlog&flight_id=f060426t01",
                          |               "title":"flight_log",
                          |               "description":"HTML page with table listing the runs for this flight.",
                          |               "roles":[
                          |
                          |               ],
                          |               "type":"text/html"
                          |            },
                          |            "kml_outline":{
                          |               "href":"http://aviris.jpl.nasa.gov/aviris_locator/y06_KML/f060426t01p00r03_sc01_outline_KML.kml",
                          |               "title":"kml_outline",
                          |               "description":"KML file describing the flight outline",
                          |               "roles":[
                          |
                          |               ],
                          |               "type":"application/vnd.google-earth.kml+xml"
                          |            }
                          |         },
                          |         "collection":"aviris_2006_60426",
                          |         "properties":{
                          |            "YY":6,
                          |            "Run":3,
                          |            "Tape":"t01",
                          |            "Year":2006,
                          |            "Scene":"sc01",
                          |            "Flight":60426,
                          |            "GEO Ver":"ort",
                          |            "RDN Ver":"c",
                          |            "Comments":"Alt = 21Kft<br>SOG = 103 kts<br>CLEAR !!",
                          |            "NASA Log":"6T010",
                          |            "Rotation":0,
                          |            "datetime":"2006-04-26T17:52:00Z",
                          |            "Flight ID":"f060426t01",
                          |            "Site Name":"Red Mtn Pass 1, CO",
                          |            "Pixel Size":2.1,
                          |            "Flight Scene":"f060426t01p00r03_sc01",
                          |            "Investigator":"Thomas Painter",
                          |            "Solar Azimuth":139.9,
                          |            "Number of Lines":6688,
                          |            "Solar Elevation":60.21,
                          |            "File Size (Bytes)":7475366912,
                          |            "Number of Samples":1335,
                          |            "Max Scene Elevation":4097.59,
                          |            "Min Scene Elevation":3163.91,
                          |            "Mean Scene Elevation":3680.71,
                          |            "Gzip File Size (Bytes)":2673260903
                          |         }
                          |      }
                          |   ]
                          |}
                          |""".stripMargin).valueOr(throw _)),
            StatusCode.Ok,
            ""
          )
        )
      }

  describe("StacClientSpec") {
    it("SttpBackendStub collections") {
      SttpStacClient(backend, uri"http://localhost:9090").collections
        .map(_.map(_.id) shouldBe "aviris_2006" :: Nil)
    }

    it("SttpBackendStub items") {
      SttpStacClient(backend, uri"http://localhost:9090")
        .items(NonEmptyString.unsafeFrom("aviris_2006"))
        .map(_.map(_.id) shouldBe "aviris_f060426t01p00r03_sc01" :: Nil)
    }

    ignore("AsyncHttpClientCatsBackend") {

      val res = AsyncHttpClientCatsBackend[IO]().flatMap { backend =>
        val client = SttpStacClient(backend, uri"http://localhost:9090")
        client.collections
      }

      res map (_ shouldNot be(Empty))
    }

    ignore("Http4sBackend") {
      val res = Blocker[IO].flatMap(Http4sBackend.usingDefaultClientBuilder[IO](_)).use { backend =>
        val client = SttpStacClient(backend, uri"http://localhost:9090")
        client.collections
      }

      res map (_ shouldNot be(Empty))
    }
  }
}
