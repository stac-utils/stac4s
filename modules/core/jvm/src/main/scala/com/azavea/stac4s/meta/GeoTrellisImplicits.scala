package com.azavea.stac4s.meta

import cats.Eq
import geotrellis.vector.Geometry
import geotrellis.vector.io.json.GeometryFormats

trait GeoTrellisImplicits extends GeometryFormats {
  implicit val eqGeometry: Eq[Geometry] = Eq.fromUniversalEquals
}
