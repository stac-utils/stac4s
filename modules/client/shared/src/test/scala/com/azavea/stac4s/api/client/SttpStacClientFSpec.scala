package com.azavea.stac4s.api.client

import com.azavea.stac4s.{ItemCollection, StacCollection, StacItem}

import cats.syntax.either._
import eu.timepit.refined.types.all.NonEmptyString
import io.circe.syntax._
import io.circe.{JsonObject, parser}
import org.scalacheck.Arbitrary
import org.scalacheck.resample._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3.testing.SttpBackendStub
import sttp.client3.{Response, StringBody}
import sttp.model.Method
import sttp.monad.EitherMonad

trait SttpStacClientFSpec[S]
    extends AnyFunSpec
    with Matchers
    with BeforeAndAfterAll
    with SttpEitherInstances
    with SttpSyntax {

  def arbCollectionShort: Arbitrary[StacCollection]
  def arbItemCollectionShort: Arbitrary[ItemCollection]
  def arbItemShort: Arbitrary[StacItem]

  def client: SttpStacClientF[Either[Throwable, *], S]

  /** We use the default synchronous Either backend to use the same tests set for the Scala JS backend. */
  lazy val backend =
    SttpBackendStub(EitherMonad)
      .whenRequestMatches(_.uri.path == Seq("search"))
      .thenRespondF { _ => Response.json(arbItemCollectionShort.arbitrary.resample().asJson) }
      .whenRequestMatches {
        case req if req.method == Method.GET => req.uri.path == Seq("collections")
        case _                               => false
      }
      .thenRespondF { _ =>
        Response.json(JsonObject("collections" -> arbCollectionShort.arbitrary.sample.toList.asJson).asJson)
      }
      .whenRequestMatches {
        case req if req.method == Method.GET => req.uri.path == Seq("collections", "collection_id")
        case _                               => false
      }
      .thenRespondF { _ => Response.item(arbCollectionShort.arbitrary.resample()) }
      .whenRequestMatches {
        case req if req.method == Method.GET => req.uri.path == Seq("collections", "collection_id", "items")
        case _                               => false
      }
      .thenRespondF { _ => Response.json(arbItemCollectionShort.arbitrary.sample.asJson) }
      .whenRequestMatches {
        case req if req.method == Method.GET => req.uri.path == Seq("collections", "collection_id", "items", "item_id")
        case _                               => false
      }
      .thenRespondF { _ => Response.item(arbItemShort.arbitrary.resample()) }
      .whenRequestMatches {
        case req if req.method == Method.PUT => req.uri.path == Seq("collections", "collection_id", "items", "item_id")
        case _                               => false
      }
      .thenRespondF { req =>
        req.body match {
          case sb: StringBody => Response.item(parser.parse(sb.s).flatMap(_.as[StacItem]).valueOr(throw _))
          case _              => Response.item(arbItemShort.arbitrary.resample())
        }
      }
      .whenRequestMatches {
        case req if req.method == Method.PATCH =>
          req.uri.path == Seq("collections", "collection_id", "items", "item_id")
        case _ => false
      }
      .thenRespondF { _ => Response.item(arbItemShort.arbitrary.resample()) }
      .whenRequestMatches {
        case req if req.method == Method.DELETE =>
          req.uri.path == Seq("collections", "collection_id", "items", "item_id")
        case _ => false
      }
      .thenRespondF { _ => Response.empty }
      .whenRequestMatches {
        case req if req.method == Method.POST => req.uri.path == Seq("collections", "collection_id", "items")
        case _                                => false
      }
      .thenRespondF { req =>
        req.body match {
          case sb: StringBody => Response.item(parser.parse(sb.s).flatMap(_.as[StacItem]).valueOr(throw _))
          case _              => Response.item(arbItemShort.arbitrary.resample())
        }
      }
      .whenRequestMatches {
        case req if req.method == Method.POST => req.uri.path == Seq("collections")
        case _                                => false
      }
      .thenRespondF { req =>
        req.body match {
          case sb: StringBody => Response.item(parser.parse(sb.s).flatMap(_.as[StacCollection]).valueOr(throw _))
          case _              => Response.item(arbCollectionShort.arbitrary.resample())
        }

      }

  describe("SttpStacClientSpec") {
    val collectionId = NonEmptyString.unsafeFrom("collection_id")
    val itemId       = NonEmptyString.unsafeFrom("item_id")

    it("search") {
      client.search.compile.toList
        .valueOr(throw _)
    }

    it("collections") {
      client.collections.compile.toList
        .valueOr(throw _)
        .map(_.id should not be empty)
    }

    it("collection") {
      client
        .collection(collectionId)
        .valueOr(throw _)
        .id should not be empty
    }

    it("collectionCreate") {
      client
        .collectionCreate(arbCollectionShort.arbitrary.resample())
        .valueOr(throw _)
        .id should not be empty
    }

    it("items") {
      client
        .items(collectionId)
        .compile
        .toList
        .valueOr(throw _)
        .map(_.id should not be empty)
    }

    it("item") {
      client
        .item(collectionId, itemId)
        .valueOr(throw _)
        .entity
        .id should not be empty
    }

    it("itemCreate") {
      val item = arbItemShort.arbitrary.resample().etag
      client
        .itemCreate(collectionId, item.entity)
        .valueOr(throw _) should be(item)
    }

    it("itemUpdate") {
      val item = arbItemShort.arbitrary.resample().copy(id = "item_id").etag
      client
        .itemUpdate(collectionId, item)
        .valueOr(throw _) should be(item)
    }

    it("itemPatch") {
      val patch = JsonObject("properties" -> Map("key" -> "value").asJson).asJson.etag
      client
        .itemPatch(collectionId, itemId, patch)
        .valueOr(throw _)
        .entity
        .id should not be empty
    }

    it("itemDelete") {
      client
        .itemDelete(collectionId, itemId)
        .valueOr(throw _)
        .valueOr(str => throw new Exception(str)) should be(empty)
    }
  }

  override def afterAll(): Unit = backend.close().valueOr(throw _)
}
