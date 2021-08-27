package com.azavea.stac4s

import com.azavea.stac4s.geometry.Geometry
import com.azavea.stac4s.testing.JsInstances._

import io.circe.syntax._
import io.circe.testing.{ArbitraryInstances, CodecTests}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.Checkers
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

import java.time.Instant

class JsSerDeSpec extends AnyFunSuite with FunSuiteDiscipline with Checkers with Matchers with ArbitraryInstances {
  checkAll("Codec.ItemCollection", CodecTests[ItemCollection].unserializableCodec)
  checkAll("Codec.StacItem", CodecTests[StacItem].unserializableCodec)
  checkAll("Codec.Geometry", CodecTests[Geometry].unserializableCodec)
  checkAll("Codec.ItemDatetime", CodecTests[ItemDatetime].unserializableCodec)
  checkAll("Codec.ItemProperties", CodecTests[ItemProperties].unserializableCodec)

  /** Ensure that the datetime field is present but null for time ranges
    *
    * Specification:
    * https://github.com/radiantearth/stac-spec/blob/v1.0.0-rc.4/item-spec/common-metadata.md#date-and-time-range
    */
  test("Encoded time ranges print null datetime") {
    val tr = ItemDatetime.TimeRange(
      Instant.parse("2021-01-01T00:00:00Z"),
      Instant.parse("2022-01-01T00:00:00Z")
    )

    val js = tr.asJson

    js.as[Map[String, Option[Instant]]]
      .fold(
        _ => fail(s"Encoded value was not decodable as a map of strings to optional instants: ${js.noSpaces}"),
        m => m.get("datetime") must equal(Some(Option.empty[Instant]))
      )
  }

}
