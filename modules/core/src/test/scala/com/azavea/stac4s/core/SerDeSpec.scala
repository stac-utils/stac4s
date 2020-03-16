package com.azavea.stac4s

import com.azavea.stac4s.meta._
import com.azavea.stac4s.Generators._

import geotrellis.vector.Geometry
import io.circe.testing.{ArbitraryInstances, CodecTests}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.Checkers
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

import java.time.Instant

class SerDeSpec extends AnyFunSuite with FunSuiteDiscipline with Checkers with ArbitraryInstances {

  test("SerDe round trips work") {
    checkAll("Codec.StacMediaType", CodecTests[StacMediaType].codec)
    checkAll("Codec.StacMediaType", CodecTests[StacMediaType].codec)
    checkAll("Codec.StacAssetRole", CodecTests[StacAssetRole].codec)
    checkAll("Codec.StacLinkType", CodecTests[StacLinkType].codec)
    checkAll("Codec.StacProviderRole", CodecTests[StacProviderRole].codec)

    checkAll("Codec.Instant", CodecTests[Instant].codec)
    checkAll("Codec.Geometry", CodecTests[Geometry].codec)

    checkAll("Codec.StacItemAsset", CodecTests[StacItemAsset].codec)
    checkAll("Codec.StacCollectionAsset", CodecTests[StacCollectionAsset].codec)

    checkAll("Codec.SPDX", CodecTests[SPDX].codec)

    checkAll("Codec.StacItem", CodecTests[StacItem].codec)

    checkAll("Codec.ItemCollection", CodecTests[ItemCollection].codec)

    checkAll("Codec.StacCatalog", CodecTests[StacCatalog].codec)

    checkAll("Codec.TwoDimBbox", CodecTests[TwoDimBbox].codec)

    checkAll("Codec.ThreeDimBbox", CodecTests[ThreeDimBbox].codec)

    checkAll("Codec.TemporalExtent", CodecTests[TemporalExtent].codec)
    checkAll("Codec.Bbox", CodecTests[Bbox].codec)
    checkAll("Codec.StacExtent", CodecTests[StacExtent].codec)

    checkAll("Codec.StacCollection", CodecTests[StacCollection].codec)
  }

}
