import sbt._
import sbt.Keys._

object Versions {

  private def ver(for212: String, for213: String) = Def.setting {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) => for212
      case Some((2, 13)) => for213
      case _             => sys.error("not good")
    }
  }

  val Cats                    = "2.13.0"
  val Circe                   = "0.14.14"
  val CirceRefined            = "0.15.1"
  val CirceJsonSchema         = "0.2.0"
  val DisciplineScalatest     = "2.3.0"
  val Enumeratum              = "1.9.0"
  val GeoTrellis              = "3.8.0"
  val Jts                     = "1.20.0"
  val Monocle                 = "2.1.0"
  val Refined                 = "0.11.3"
  val ScalacheckCats          = "0.3.2"
  val Scalacheck              = "1.18.1"
  val ScalatestPlusScalacheck = "3.2.14.0"
  val Scalatest               = "3.2.19"
  val Scapegoat               = "3.1.9"
  val Shapeless               = "2.3.13"
  val Sttp                    = "3.11.0"
  val SttpModel               = "1.7.16"
  val SttpShared              = "1.5.0"
  val Fs2                     = "3.12.2"
  val ThreeTenExtra           = "1.8.0"
  val ScalaJavaTime           = "2.6.0"
}
