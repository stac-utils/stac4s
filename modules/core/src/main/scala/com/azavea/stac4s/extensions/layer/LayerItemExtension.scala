package com.azavea.stac4s.extensions.layer

import com.azavea.stac4s.extensions.ItemExtension

import cats.data.NonEmptyList
import cats.Eq
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.{Decoder, Encoder}
import io.circe.refined._
import io.circe.syntax._

case class LayerItemExtension(ids: NonEmptyList[NonEmptyString])

object LayerItemExtension {
  implicit val eqLayerProperties: Eq[LayerItemExtension] = Eq.fromUniversalEquals

  implicit val encLayerProperties: Encoder.AsObject[LayerItemExtension] =
    Encoder.AsObject.instance[LayerItemExtension] { o => Map("layer:ids" -> o.ids.asJson).asJsonObject }

  implicit val decLayerProperties: Decoder[LayerItemExtension] = Decoder[Map[String, NonEmptyList[NonEmptyString]]] emap {
    _.get("layer:ids") match {
      case Some(l) => Right(LayerItemExtension(l))
      case _       => Left("Could not decode LayerProperties.")
    }
  }

  implicit val itemExtension: ItemExtension[LayerItemExtension] = ItemExtension.instance
}
