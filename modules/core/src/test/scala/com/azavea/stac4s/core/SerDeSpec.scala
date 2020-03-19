package com.azavea.stac4s

import com.azavea.stac4s.extensions.label._
import com.azavea.stac4s.meta._
import com.azavea.stac4s.Generators._

import geotrellis.vector.Geometry
import io.circe.parser._
import io.circe.testing.{ArbitraryInstances, CodecTests}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.Checkers
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

import java.time.Instant

class SerDeSpec extends AnyFunSuite with FunSuiteDiscipline with Checkers with Matchers with ArbitraryInstances {

  // core
  checkAll("Codec.Bbox", CodecTests[Bbox].unserializableCodec)
  checkAll("Codec.Geometry", CodecTests[Geometry].unserializableCodec)
  checkAll("Codec.Instant", CodecTests[Instant].unserializableCodec)
  checkAll("Codec.ItemCollection", CodecTests[ItemCollection].unserializableCodec)
  checkAll("Codec.SPDX", CodecTests[SPDX].unserializableCodec)
  checkAll("Codec.StacAssetRole", CodecTests[StacAssetRole].unserializableCodec)
  checkAll("Codec.StacCatalog", CodecTests[StacCatalog].unserializableCodec)
  checkAll("Codec.StacCollection", CodecTests[StacCollection].unserializableCodec)
  checkAll("Codec.StacCollectionAsset", CodecTests[StacCollectionAsset].unserializableCodec)
  checkAll("Codec.StacExtent", CodecTests[StacExtent].unserializableCodec)
  checkAll("Codec.StacItem", CodecTests[StacItem].unserializableCodec)
  checkAll("Codec.StacItemAsset", CodecTests[StacItemAsset].unserializableCodec)
  checkAll("Codec.StacLinkType", CodecTests[StacLinkType].unserializableCodec)
  checkAll("Codec.StacMediaType", CodecTests[StacMediaType].unserializableCodec)
  checkAll("Codec.StacProviderRole", CodecTests[StacProviderRole].unserializableCodec)
  checkAll("Codec.TemporalExtent", CodecTests[TemporalExtent].unserializableCodec)
  checkAll("Codec.ThreeDimBbox", CodecTests[ThreeDimBbox].unserializableCodec)
  checkAll("Codec.TwoDimBbox", CodecTests[TwoDimBbox].unserializableCodec)

  // extensions

  // label extension
  checkAll("Codec.LabelClass", CodecTests[LabelClass].unserializableCodec)
  checkAll("Codec.LabelClassClasses", CodecTests[LabelClassClasses].unserializableCodec)
  checkAll("Codec.LabelClassName", CodecTests[LabelClassName].unserializableCodec)
  checkAll("Codec.LabelCount", CodecTests[LabelCount].unserializableCodec)
  checkAll("Codec.LabelExtensionProperties", CodecTests[LabelExtensionProperties].unserializableCodec)
  checkAll("Codec.LabelMethod", CodecTests[LabelMethod].unserializableCodec)
  checkAll("Codec.LabelOverview", CodecTests[LabelOverview].unserializableCodec)
  checkAll("Codec.LabelProperties", CodecTests[LabelProperties].unserializableCodec)
  checkAll("Codec.LabelStats", CodecTests[LabelStats].unserializableCodec)
  checkAll("Codec.LabelTask", CodecTests[LabelTask].unserializableCodec)
  checkAll("Codec.LabelType", CodecTests[LabelType].unserializableCodec)

  // unit tests
  test("ignore optional fields") {
    val link =
      decode[StacLink]("""{"href":"s3://foo/item.json","rel":"item"}""")
    link map { _.labelExtAssets } shouldBe Right(List.empty[String])
  }
}
