package com.azavea.stac4s

import com.azavea.stac4s.extensions.asset._
import com.azavea.stac4s.extensions.eo._
import com.azavea.stac4s.extensions.label._
import com.azavea.stac4s.extensions.layer._
import com.azavea.stac4s.meta.ForeignImplicits._
import com.azavea.stac4s.testing.TestInstances._

import io.circe.Decoder
import io.circe.parser._
import io.circe.syntax._
import io.circe.testing.{ArbitraryInstances, CodecTests}
import org.scalatest.Assertion
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.Checkers
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

import java.time.{Instant, OffsetDateTime}

class SerDeSpec extends AnyFunSuite with FunSuiteDiscipline with Checkers with Matchers with ArbitraryInstances {

  // core
  checkAll("Codec.Bbox", CodecTests[Bbox].unserializableCodec)
  checkAll("Codec.SPDX", CodecTests[SPDX].unserializableCodec)
  checkAll("Codec.StacAssetRole", CodecTests[StacAssetRole].unserializableCodec)
  checkAll("Codec.StacCatalog", CodecTests[StacCatalog].unserializableCodec)
  checkAll("Codec.StacCollectionAsset", CodecTests[StacCollectionAsset].unserializableCodec)
  checkAll("Codec.StacAsset", CodecTests[StacAsset].unserializableCodec)
  checkAll("Codec.StacLinkType", CodecTests[StacLinkType].unserializableCodec)
  checkAll("Codec.StacMediaType", CodecTests[StacMediaType].unserializableCodec)
  checkAll("Codec.StacProviderRole", CodecTests[StacProviderRole].unserializableCodec)
  checkAll("Codec.ThreeDimBbox", CodecTests[ThreeDimBbox].unserializableCodec)
  checkAll("Codec.TwoDimBbox", CodecTests[TwoDimBbox].unserializableCodec)

  // extensions

  // label extension
  checkAll("Codec.LabelClass", CodecTests[LabelClass].unserializableCodec)
  checkAll("Codec.LabelClassClasses", CodecTests[LabelClassClasses].unserializableCodec)
  checkAll("Codec.LabelClassName", CodecTests[LabelClassName].unserializableCodec)
  checkAll("Codec.LabelCount", CodecTests[LabelCount].unserializableCodec)
  checkAll("Codec.LabelExtensionProperties", CodecTests[LabelItemExtension].unserializableCodec)
  checkAll("Codec.LabelMethod", CodecTests[LabelMethod].unserializableCodec)
  checkAll("Codec.LabelOverview", CodecTests[LabelOverview].unserializableCodec)
  checkAll("Codec.LabelProperties", CodecTests[LabelProperties].unserializableCodec)
  checkAll("Codec.LabelStats", CodecTests[LabelStats].unserializableCodec)
  checkAll("Codec.LabelTask", CodecTests[LabelTask].unserializableCodec)
  checkAll("Codec.LabelType", CodecTests[LabelType].unserializableCodec)

  // Layer extension
  checkAll("Codec.LayerProperties", CodecTests[LayerItemExtension].unserializableCodec)
  checkAll("Codec.StacLayerProperties", CodecTests[StacLayerProperties].unserializableCodec)

  // eo extension
  checkAll("Codec.EOBand", CodecTests[Band].unserializableCodec)
  checkAll("Codec.EOItemExtension", CodecTests[EOItemExtension].unserializableCodec)
  checkAll("Codec.EOAssetExtension", CodecTests[EOAssetExtension].unserializableCodec)

  // unit tests
  test("ignore optional fields") {
    val link =
      decode[StacLink]("""{"href":"s3://foo/item.json","rel":"item"}""")
    link map { _.extensionFields } shouldBe Right(().asJsonObject)
  }

  // timezone parsing unit tests
  private def getTimeDecodeTest(timestring: String): Assertion =
    timestring.asJson.as[Instant] shouldBe Right(OffsetDateTime.parse(timestring, RFC3339formatter).toInstant)

  private def accumulatingDecodeTest[T: Decoder]: Assertion =
    decodeAccumulating[T]("{}").fold(
      errs => {
        println(s"Errs: $errs")
        errs.size should be > 1
      },
      _ => fail("Decoding succeeded but should not have")
    )

  test("Instant decodes timestrings with +0x:00 timezones") {
    getTimeDecodeTest("2018-01-01T00:00:00+05:00")
  }

  test("Instant decodes timestrings with -0x:00 timezones") {
    getTimeDecodeTest("2018-01-01T00:00:00-09:00")
  }

  test("Instant decodes timestrings with 0000 format timezone") {
    getTimeDecodeTest("2018-01-01T00:00:00+0000")
  }

  test("Instant decodes timestrings with +00 format timezone") {
    getTimeDecodeTest("2018-01-01T00:00:00+00")
  }

  test("Instant decodes timestrings with -00 format timezone") {
    getTimeDecodeTest("2018-01-01T00:00:00-00")
  }

  test("Instant decodes timestring with Z format timezone") {
    getTimeDecodeTest("2020-04-03T11:32:26Z")
  }

  test("Instant decodes timestring with 1e3 Z format timezone") {
    getTimeDecodeTest("2018-04-03T11:32:26.553Z")
  }

  test("Instant decodes timestring with 1e9 Z format timezone") {
    getTimeDecodeTest("2018-04-03T11:32:26.553955473Z")
  }

  test("Collections accumulate errors") {
    accumulatingDecodeTest[StacCollection]
  }

  test("Items accumulate errors") {
    accumulatingDecodeTest[StacItem]
  }

  test("Catalogs accumulate errors") {
    accumulatingDecodeTest[StacCatalog]
  }

  test("Links accumulate errors") {
    accumulatingDecodeTest[StacLink]
  }

  test("Assets accumulate errors") {
    // StacAssets actually only have one required field, so an empty json object only
    // decodes with one error even with accumulation
    decodeAccumulating[StacAsset]("""{"href": 1234, "title": 1234}""")
      .fold(errs => errs.size shouldBe 2, _ => fail("Decoding should not have succeeded"))
  }

  test("Extents accumulate errors") {
    accumulatingDecodeTest[StacExtent]
  }
}
