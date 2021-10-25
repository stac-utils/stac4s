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
  val Jts                     = Def.setting(ver("1.16.1", "1.17.0").value)
  val Monocle                 = "2.1.0"
  val Refined                 = "0.9.27"
  val ScalacheckCats          = "0.3.1"
  val Scalacheck              = "1.15.4"
  val ScalatestPlusScalacheck = "3.2.2.0"
  val Scalatest               = "3.2.10"
  val Scapegoat               = "1.4.10"
  val Shapeless               = "2.3.7"
  val Sttp                    = "3.3.16"
  val SttpModel               = "1.4.16"
  val SttpShared              = "1.2.7"
  val Fs2                     = "3.2.1"
  val ThreeTenExtra           = "1.7.0"
}
