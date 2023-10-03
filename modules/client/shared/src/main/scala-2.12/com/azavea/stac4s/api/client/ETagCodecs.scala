package com.azavea.stac4s.api.client

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.refined._
import io.circe.{Decoder, Encoder}

trait ETagCodecs {
  implicit def encoderETag[T: Encoder]: Encoder[ETag[T]] = deriveEncoder
  implicit def decoderETag[T: Decoder]: Decoder[ETag[T]] = deriveDecoder
}
