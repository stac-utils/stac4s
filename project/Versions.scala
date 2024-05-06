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

  val Cats                    = "2.10.0"
  val Circe                   = "0.14.7"
  val CirceJsonSchema         = "0.2.0"
  val DisciplineScalatest     = "2.2.0"
  val Enumeratum              = "1.7.3"
  val GeoTrellis              = "3.7.1"
  val Jts                     = "1.19.0"
  val Monocle                 = "2.1.0"
  val Refined                 = "0.11.1"
  val ScalacheckCats          = "0.3.2"
  val Scalacheck              = "1.18.0"
  val ScalatestPlusScalacheck = "3.2.14.0"
  val Scalatest               = "3.2.18"
  val Scapegoat               = "2.1.5"
  val Shapeless               = "2.3.10"
  val Sttp                    = "3.9.6"
  val SttpModel               = "1.7.10"
  val SttpShared              = "1.3.17"
  val Fs2                     = "3.10.2"
  val ThreeTenExtra           = "1.8.0"
  val ScalaJavaTime           = "2.5.0"
}
