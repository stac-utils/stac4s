package com.azavea.stac4s.api.client

import cats.syntax.either._
import eu.timepit.refined.types.numeric.PosInt
import io.circe.generic.semiauto._
import io.circe.parser.parse
import io.circe.refined._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Error}

import java.time.Instant
import java.util.Base64

final case class PaginationToken(timestampAtLeast: Instant, serialIdGreaterThan: PosInt)

/** Circe codecs should encode token into a base64 string
  * https://github.com/azavea/franklin/blob/f5be8ddf48661c5bc43cbd22cb7277e961641803/application/src/main/scala/com/azavea/franklin/api/schemas/package.scala#L84-L85
  */
object PaginationToken {
  val defaultDecoder: Decoder[PaginationToken] = deriveDecoder
  val defaultEncoder: Encoder[PaginationToken] = deriveEncoder

  val b64Encoder: Base64.Encoder = Base64.getEncoder
  val b64Decoder: Base64.Decoder = Base64.getDecoder

  def encPaginationToken(token: PaginationToken): String = b64Encoder.encodeToString(
    token.asJson(defaultEncoder).noSpaces.getBytes
  )

  def decPaginationTokenEither(encoded: String): Either[Error, PaginationToken] = {
    val jsonString = new String(b64Decoder.decode(encoded))
    for {
      js      <- parse(jsonString)
      decoded <- js.as[PaginationToken](defaultDecoder)
    } yield decoded
  }

  implicit val paginationTokenDecoder: Decoder[PaginationToken] =
    Decoder.decodeString.emap(str => decPaginationTokenEither(str).leftMap(_.getMessage))

  implicit val paginationTokenEncoder: Encoder[PaginationToken] = { encPaginationToken(_).asJson }
}
