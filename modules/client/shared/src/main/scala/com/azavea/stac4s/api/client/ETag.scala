package com.azavea.stac4s.api.client

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.refined._
import io.circe.{Decoder, Encoder}

case class ETag[T](entity: T, tag: Option[NonEmptyString])

object ETag {
  implicit def encoderETag[T: Encoder]: Encoder[ETag[T]] = deriveEncoder
  implicit def decoderETag[T: Decoder]: Decoder[ETag[T]] = deriveDecoder
}
