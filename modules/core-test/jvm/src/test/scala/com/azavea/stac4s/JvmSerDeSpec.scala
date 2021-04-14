package com.azavea.stac4s

import com.azavea.stac4s.meta._
import com.azavea.stac4s.testing.JvmInstances._
import com.azavea.stac4s.jvmTypes._

import geotrellis.vector.Geometry
import io.circe.testing.{ArbitraryInstances, CodecTests}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.Checkers
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

import java.time.Instant
import com.azavea.stac4s.extensions.layer.StacLayer
import org.threeten.extra.PeriodDuration
import com.azavea.stac4s.extensions.periodic.PeriodicExtent

class JvmSerDeSpec extends AnyFunSuite with FunSuiteDiscipline with Checkers with Matchers with ArbitraryInstances {
  checkAll("Codec.ItemCollection", CodecTests[ItemCollection].unserializableCodec)
  checkAll("Codec.StacItem", CodecTests[StacItem].unserializableCodec)
  checkAll("Codec.Geometry", CodecTests[Geometry].unserializableCodec)
  checkAll("Codec.Instant", CodecTests[Instant].unserializableCodec)
  checkAll("Codec.StacCollection", CodecTests[StacCollection].unserializableCodec)
  checkAll("Codec.Interval", CodecTests[Interval].unserializableCodec)
  checkAll("Codec.StacExtent", CodecTests[StacExtent].unserializableCodec)
  checkAll("Codec.TemporalExtent", CodecTests[TemporalExtent].unserializableCodec)
  checkAll("Codec.StacLayer", CodecTests[StacLayer].unserializableCodec)
  checkAll("Codec.PeriodDuration", CodecTests[PeriodDuration].unserializableCodec)
  checkAll("Codec.PeriodicExtent", CodecTests[PeriodicExtent].unserializableCodec)
  checkAll("Codec.ItemDateTime", CodecTests[ItemDateTime].unserializableCodec)
}
