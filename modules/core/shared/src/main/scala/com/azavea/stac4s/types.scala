package com.azavea.stac4s

import cats.data.Ior
import cats.kernel.Eq
import eu.timepit.refined.W
import eu.timepit.refined.api.Refined
import eu.timepit.refined.generic._
import io.circe.syntax._
import io.circe.{Decoder, DecodingFailure, Encoder, HCursor}

package object types {

  type CatalogType    = String Refined Equal[W.`"Catalog"`.T]
  type CollectionType = String Refined Equal[W.`"Collection"`.T]

  type ItemDatetime = Ior[PointInTime, TimeRange]

  implicit val encItemDateTime: Encoder[ItemDatetime] = {
    case Ior.Left(pit @ PointInTime(_))                       => pit.asJson
    case Ior.Right(tr @ TimeRange(_, _))                      => tr.asJson
    case Ior.Both(pit @ PointInTime(_), tr @ TimeRange(_, _)) => pit.asJson.deepMerge(tr.asJson)
  }

  implicit val decItemDateTime: Decoder[ItemDatetime] = { c: HCursor =>
    (c.as[PointInTime], c.as[TimeRange]) match {
      case (Right(pit), Right(tr)) => Right(Ior.Both(pit, tr))
      case (_, Right(tr))          => Right(Ior.Right(tr))
      case (Right(pit), _)         => Right(Ior.Left(pit))
      case (Left(err1), Left(err2)) =>
        (err1, err2) match {
          case (DecodingFailure(decFailure1, h1), DecodingFailure(decFailure2, h2)) =>
            Left(DecodingFailure(s"${decFailure1}. ${decFailure2}", h1 ++ h2))
          // since they're decoding the same cursor, if one of the errors is a ParsingFailure instead
          // of a decoding failure, they _both_ should be, so we just need the first one
          case _ =>
            Left(err1)
        }
    }
  }

  implicit val eqItemDatetime: Eq[ItemDatetime] = Eq.fromUniversalEquals
}
