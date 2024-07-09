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

  val Cats                    = "2.12.0"
  val Circe                   = "0.14.9"
  val CirceJsonSchema         = "0.2.0"
  val DisciplineScalatest     = "2.3.0"
  val Enumeratum              = "1.7.3"
  val GeoTrellis              = "3.7.1"
  val Jts                     = "1.19.0"
  val Monocle                 = "2.1.0"
  val Refined                 = "0.11.2"
  val ScalacheckCats          = "0.3.2"
  val Scalacheck              = "1.18.0"
  val ScalatestPlusScalacheck = "3.2.14.0"
  val Scalatest               = "3.2.19"
  val Scapegoat               = "2.1.6"
  val Shapeless               = "2.3.12"
  val Sttp                    = "3.9.7"
  val SttpModel               = "1.7.11"
  val SttpShared              = "1.3.19"
  val Fs2                     = "3.10.2"
  val ThreeTenExtra           = "1.8.0"
  val ScalaJavaTime           = "2.6.0"
}
