package com.azavea.stac4s.api.client

import com.azavea.stac4s.testing.JsInstances

import cats.syntax.either._
import eu.timepit.refined.types.all.NonEmptyString
import io.circe.JsonObject
import io.circe.syntax._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3.testing.SttpBackendStub
import sttp.client3.{Response, UriContext}
import sttp.model.Method
import sttp.monad.EitherMonad

class StacClientSpec extends AnyFunSpec with Matchers with JsInstances with BeforeAndAfterAll {

  lazy val backend =
    SttpBackendStub(EitherMonad)
      .whenRequestMatches(_.uri.path == Seq("search"))
      .thenRespondF { _ =>
        Response
          .ok(arbItemCollectionShort.arbitrary.sample.asJson.asRight)
          .asRight
      }
      .whenRequestMatches {
        case req if req.method == Method.GET => req.uri.path == Seq("collections")
        case _                               => false
      }
      .thenRespondF { _ =>
        Response
          .ok(JsonObject("collections" -> arbCollectionShort.arbitrary.sample.toList.asJson).asJson.asRight)
          .asRight
      }
      .whenRequestMatches {
        case req if req.method == Method.GET => req.uri.path == Seq("collections", "collection_id", "items")
        case _                               => false
      }
      .thenRespondF { _ =>
        Response
          .ok(arbItemCollectionShort.arbitrary.sample.asJson.asRight)
          .asRight
      }
      .whenRequestMatches(_.uri.path == Seq("collections", "collection_id", "items", "item_id"))
      .thenRespondF { _ =>
        Response
          .ok(arbItemShort.arbitrary.sample.asRight)
          .asRight
      }
      .whenRequestMatches {
        case req if req.method == Method.POST => req.uri.path == Seq("collections", "collection_id", "items")
        case _                                => false
      }
      .thenRespondF { _ =>
        Response
          .ok(arbItemShort.arbitrary.sample.get.asRight)
          .asRight
      }
      .whenRequestMatches {
        case req if req.method == Method.POST => req.uri.path == Seq("collections")
        case _                                => false
      }
      .thenRespondF { _ =>
        Response
          .ok(arbCollectionShort.arbitrary.sample.get.asRight)
          .asRight
      }

  describe("StacClientSpec") {
    it("search") {
      SttpStacClient(backend, uri"http://localhost:9090")
        .search()
        .valueOr(throw _)
        .size should be > 0
    }

    it("collections") {
      SttpStacClient(backend, uri"http://localhost:9090").collections
        .valueOr(throw _)
        .size should be > 0
    }

    it("items") {
      SttpStacClient(backend, uri"http://localhost:9090")
        .items(NonEmptyString.unsafeFrom("collection_id"))
        .valueOr(throw _)
        .size should be > 0
    }

    it("item") {
      SttpStacClient(backend, uri"http://localhost:9090")
        .item(NonEmptyString.unsafeFrom("collection_id"), NonEmptyString.unsafeFrom("item_id"))
        .valueOr(throw _)
        .size should be > 0
    }

    it("itemCreate") {
      SttpStacClient(backend, uri"http://localhost:9090")
        .itemCreate(NonEmptyString.unsafeFrom("collection_id"), arbItemShort.arbitrary.sample.get)
        .map(_.id should not be empty)
    }

    it("collectionCreate") {
      SttpStacClient(backend, uri"http://localhost:9090")
        .collectionCreate(arbCollectionShort.arbitrary.sample.get)
        .map(_.id should not be empty)
    }
  }

  override def afterAll(): Unit = backend.close().valueOr(throw _)
}
