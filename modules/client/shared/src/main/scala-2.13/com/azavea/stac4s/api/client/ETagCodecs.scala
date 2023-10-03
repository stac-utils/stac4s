package com.azavea.stac4s.api.client

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.refined._
import io.circe.{Decoder, Encoder}

import scala.annotation.unused

trait ETagCodecs {
  implicit def encoderETag[T](implicit @unused e: Encoder[T]): Encoder[ETag[T]] = deriveEncoder
  implicit def decoderETag[T](implicit @unused d: Decoder[T]): Decoder[ETag[T]] = deriveDecoder
}
