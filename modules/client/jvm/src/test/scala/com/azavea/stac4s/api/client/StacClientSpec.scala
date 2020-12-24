package com.azavea.stac4s.api.client

import com.azavea.stac4s.testing.JvmInstances
import cats.effect.{Blocker, IO}
import cats.syntax.applicative._
import cats.syntax.either._
import com.azavea.IOSpec
import eu.timepit.refined.collection.Empty
import eu.timepit.refined.types.all.NonEmptyString
import io.circe.JsonObject
import io.circe.syntax._
import org.scalatest.BeforeAndAfterAll
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client3.http4s.Http4sBackend
import sttp.client3.impl.cats.CatsMonadAsyncError
import sttp.client3.testing.SttpBackendStub
import sttp.client3.{Response, UriContext}

class StacClientSpec extends IOSpec with JvmInstances with BeforeAndAfterAll {

  lazy val backend =
    SttpBackendStub(new CatsMonadAsyncError[IO]())
      .whenRequestMatches(_.uri.path == Seq("search"))
      .thenRespondF { _ =>
        Response
          .ok(arbItemCollectionShort.arbitrary.sample.asJson.asRight)
          .pure[IO]
      }
      .whenRequestMatches(_.uri.path == Seq("collections"))
      .thenRespondF { _ =>
        Response
          .ok(JsonObject("collections" -> arbCollectionShort.arbitrary.sample.toList.asJson).asJson.asRight)
          .pure[IO]
      }
      .whenRequestMatches(_.uri.path == Seq("collections", "collection_id", "items"))
      .thenRespondF { _ =>
        Response
          .ok(arbItemCollectionShort.arbitrary.sample.asJson.asRight)
          .pure[IO]
      }
      .whenRequestMatches(_.uri.path == Seq("collections", "collection_id", "items", "item_id"))
      .thenRespondF { _ =>
        Response
          .ok(arbItemShort.arbitrary.sample.asRight)
          .pure[IO]
      }

  describe("StacClientSpec") {
    it("search") {
      SttpStacClient(backend, uri"http://localhost:9090")
        .search()
        .map(_.size should be > 0)
    }

    it("collections") {
      SttpStacClient(backend, uri"http://localhost:9090").collections
        .map(_.size should be > 0)
    }

    it("items") {
      SttpStacClient(backend, uri"http://localhost:9090")
        .items(NonEmptyString.unsafeFrom("collection_id"))
        .map(_.size should be > 0)
    }

    it("item") {
      SttpStacClient(backend, uri"http://localhost:9090")
        .item(NonEmptyString.unsafeFrom("collection_id"), NonEmptyString.unsafeFrom("item_id"))
        .map(_.size should be > 0)
    }
  }

  describe("STAC Client Examples") {
    ignore("AsyncHttpClientCatsBackend") {

      val res = AsyncHttpClientCatsBackend[IO]().flatMap { backend =>
        SttpStacClient(backend, uri"http://localhost:9090").collections
      }

      res map (_ shouldNot be(Empty))
    }

    ignore("Http4sBackend") {
      val res = Blocker[IO].flatMap(Http4sBackend.usingDefaultClientBuilder[IO](_)).use { backend =>
        SttpStacClient(backend, uri"http://localhost:9090").collections
      }

      res map (_ shouldNot be(Empty))
    }
  }

  override def afterAll(): Unit = backend.close().unsafeRunSync()
}
