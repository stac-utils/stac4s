package com.azavea.stac4s.api.client

import cats.syntax.either._
import eu.timepit.refined.types.numeric.PosInt
import io.circe
import io.circe.generic.semiauto._
import io.circe.parser.parse
import io.circe.refined._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}

import java.time.Instant
import java.util.Base64

final case class PaginationToken(timestampAtLeast: Instant, serialIdGreaterThan: PosInt)

/** Circe codecs should encode token into a base64 string
  * https://github.com/azavea/franklin/blob/f5be8ddf48661c5bc43cbd22cb7277e961641803/application/src/main/scala/com/azavea/franklin/api/schemas/package.scala#L84-L85
  */
object PaginationToken {
  val b64Encoder = Base64.getEncoder
  val b64Decoder = Base64.getDecoder

  val defaultDecoder: Decoder[PaginationToken] = deriveDecoder
  val defaultEncoder: Encoder[PaginationToken] = deriveEncoder

  def encPaginationToken(token: PaginationToken): String = b64Encoder.encodeToString(
    token.asJson(defaultEncoder).noSpaces.getBytes
  )

  def decPaginationToken(encoded: String): Either[circe.Error, PaginationToken] = {
    val jsonString = new String(b64Decoder.decode(encoded))
    for {
      js      <- parse(jsonString)
      decoded <- js.as[PaginationToken](defaultDecoder)
    } yield decoded
  }

  implicit val dec: Decoder[PaginationToken] =
    Decoder.decodeString.emap(str => decPaginationToken(str).leftMap(_.getMessage))

  implicit val enc: Encoder[PaginationToken] = { encPaginationToken(_).asJson }
}
