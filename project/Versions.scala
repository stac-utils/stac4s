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

  val Cats                    = "2.6.1"
  val Circe                   = "0.14.1"
  val CirceJsonSchema         = "0.2.0"
  val DisciplineScalatest     = "2.1.5"
  val Enumeratum              = "1.7.0"
  val GeoTrellis              = Def.setting(ver("3.6.0", "3.6.1-SNAPSHOT").value)
  val Jts                     = "1.16.1"
  val Monocle                 = "2.1.0"
  val Refined                 = "0.9.26"
  val ScalacheckCats          = "0.3.0"
  val Scalacheck              = "1.15.4"
  val ScalatestPlusScalacheck = "3.2.2.0"
  val Scalatest               = "3.2.9"
  val Scapegoat               = "1.4.9"
  val Shapeless               = "2.3.7"
  val Sttp                    = "3.3.9"
  val SttpModel               = "1.4.7"
  val SttpShared              = "1.2.5"
  val Fs2                     = "2.5.9"
  val ThreeTenExtra           = "1.6.0"
}
