package com.azavea.stac4s.extensions.layer

import cats.Eq
import io.circe.{Decoder, Encoder}
import io.circe.syntax._

case class LayerProperties(layers: List[String])

object LayerProperties {
  implicit val eqLayerProperties: Eq[LayerProperties] = Eq.fromUniversalEquals

  implicit val encLayerProperties: Encoder.AsObject[LayerProperties] = Encoder.AsObject.instance[LayerProperties] { o =>
    Map("layers" -> o.layers.asJson).asJsonObject
  }

  implicit val decLayerProperties: Decoder[LayerProperties] = Decoder[Map[String, List[String]]] emap {
    _.get("layers") match {
      case Some(l) => Right(LayerProperties(l))
      case _       => Left("Could not decode LayerPropertirs")
    }
  }
}
