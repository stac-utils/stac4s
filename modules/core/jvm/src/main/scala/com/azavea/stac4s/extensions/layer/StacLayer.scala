package com.azavea.stac4s.extensions.layer

import geotrellis.vector.Geometry
import eu.timepit.refined.types.string
import com.azavea.stac4s.{Bbox, StacLink}
import com.azavea.stac4s.meta._
import cats.kernel.Eq
import io.circe.Encoder
import io.circe.refined._
import io.circe.Decoder

final case class StacLayer(
    id: string.NonEmptyString,
    bbox: Bbox,
    geometry: Geometry,
    properties: StacLayerProperties,
    links: List[StacLink],
    _type: String = "Feature"
)

object StacLayer {
  implicit val eqStacLayer: Eq[StacLayer] = Eq.fromUniversalEquals

  implicit val encStacLayer: Encoder[StacLayer] = Encoder.forProduct6(
    "id",
    "bbox",
    "geometry",
    "properties",
    "links",
    "type"
  )(layer => (layer.id, layer.bbox, layer.geometry, layer.properties, layer.links, layer._type))

  implicit val decStacLayer: Decoder[StacLayer] = Decoder.forProduct6(
    "id",
    "bbox",
    "geometry",
    "properties",
    "links",
    "type"
  )(StacLayer.apply)
}
