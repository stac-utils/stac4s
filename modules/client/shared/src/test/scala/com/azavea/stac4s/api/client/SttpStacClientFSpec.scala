package com.azavea.stac4s.api.client

import com.azavea.stac4s.{ItemCollection, StacCollection, StacItem}

import cats.syntax.either._
import eu.timepit.refined.types.all.NonEmptyString
import io.circe.JsonObject
import io.circe.syntax._
import org.scalacheck.Arbitrary
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3.Response
import sttp.client3.testing.SttpBackendStub
import sttp.model.Method
import sttp.monad.EitherMonad

trait SttpStacClientFSpec extends AnyFunSpec with Matchers with BeforeAndAfterAll {

  def arbCollectionShort: Arbitrary[StacCollection]
  def arbItemCollectionShort: Arbitrary[ItemCollection]
  def arbItemShort: Arbitrary[StacItem]

  def client: SttpStacClientF[Either[Throwable, *]]

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
        case req if req.method == Method.GET => req.uri.path == Seq("collections", "collection_id")
        case _                               => false
      }
      .thenRespondF { _ =>
        Response
          .ok(arbCollectionShort.arbitrary.sample.toRight("Collection generation failure."))
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
          .ok(arbItemShort.arbitrary.sample.toRight("Item generation failure."))
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

  describe("SttpStacClientSpec") {
    it("search") {
      client.search
        .valueOr(throw _)
        .size should be > 0
    }

    it("collections") {
      client.collections
        .valueOr(throw _)
        .size should be > 0
    }

    it("collection") {
      client
        .collection(NonEmptyString.unsafeFrom("collection_id"))
        .valueOr(throw _)
    }

    it("items") {
      client
        .items(NonEmptyString.unsafeFrom("collection_id"))
        .valueOr(throw _)
        .size should be > 0
    }

    it("item") {
      client
        .item(NonEmptyString.unsafeFrom("collection_id"), NonEmptyString.unsafeFrom("item_id"))
        .valueOr(throw _)
    }

    it("itemCreate") {
      client
        .itemCreate(NonEmptyString.unsafeFrom("collection_id"), arbItemShort.arbitrary.sample.get)
        .map(_.id should not be empty)
    }

    it("collectionCreate") {
      client
        .collectionCreate(arbCollectionShort.arbitrary.sample.get)
        .map(_.id should not be empty)
    }
  }

  override def afterAll(): Unit = backend.close().valueOr(throw _)
}
