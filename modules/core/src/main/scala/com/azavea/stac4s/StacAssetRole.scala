package com.azavea.stac4s

import cats.Eq
import io.circe.{Decoder, Encoder}

sealed abstract class StacAssetRole(val repr: String) {
  override def toString = repr
}

object StacAssetRole {

  implicit def eqStacAssetRole: Eq[StacAssetRole] =
    Eq[String].imap(fromString _)(_.repr)

  case object Thumbnail                   extends StacAssetRole("thumbnail")
  case object Overview                    extends StacAssetRole("overview")
  case object Data                        extends StacAssetRole("data")
  case object Metadata                    extends StacAssetRole("metadata")
  final case class VendorAsset(s: String) extends StacAssetRole(s)

  private def fromString(s: String): StacAssetRole = s.toLowerCase match {
    case "thumbnail" => Thumbnail
    case "overview"  => Overview
    case "data"      => Data
    case "metadata"  => Metadata
    case _           => VendorAsset(s)
  }

  implicit val encStacAssetRole: Encoder[StacAssetRole] =
    Encoder.encodeString.contramap[StacAssetRole](_.toString)

  implicit val decStacAssetRole: Decoder[StacAssetRole] =
    Decoder.decodeString.emap { str => Either.catchNonFatal(fromString(str)).leftMap(_ => "StacAssetRole") }
}
