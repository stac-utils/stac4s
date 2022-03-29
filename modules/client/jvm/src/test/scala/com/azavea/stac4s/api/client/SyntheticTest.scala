package com.azavea.stac4s.api.client

import cats.effect.IO
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client3.UriContext
import io.circe.parser._
import cats.syntax.either._

import scala.concurrent.ExecutionContext

class SyntheticTest extends AnyFunSpec with Matchers with BeforeAndAfterAll with SttpEitherInstances with SttpSyntax {
  implicit val cs = IO.contextShift(ExecutionContext.global)
  val backend     = AsyncHttpClientCatsBackend[IO]().unsafeRunSync()

  geotrellis.vector.conf.JtsConfig.conf

  describe("Integration test") {
    it("should query by bbox") {
      val filtersString =
        """
          |{
          |    "collections": ["sentinel-s2-l2a-cogs"],
          |    "datetime": "2021-06-01T19:09:23.735395Z/2021-06-30T19:09:23.735395Z",
          |    "bbox": [-92.2646, 46.6930, -92.0276, 46.9739]
          |}""".stripMargin

      val filters = parse(filtersString).flatMap(_.as[SearchFilters]).valueOr(throw _)

      val res = SttpStacClient(backend, uri"https://earth-search.aws.element84.com/v0/search")
        .search(filters)
        .take(30)
        .compile
        .toList
        .unsafeRunSync()

      println(res.length)
    }
  }

}
