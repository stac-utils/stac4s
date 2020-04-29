package com.azavea.stac4s.core

import io.circe._
import io.circe.generic.semiauto._

final case class StacProvider(
    name: String,
    description: Option[String],
    roles: List[StacProviderRole],
    url: Option[String]
)

object StacProvider {
  implicit val encStacProvider: Encoder[StacProvider] = deriveEncoder
  implicit val decStacProvider: Decoder[StacProvider] = deriveDecoder
}
