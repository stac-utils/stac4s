import sbt._
import sbt.Keys._

object Versions {
  val Cats                    = "2.6.0"
  val Circe                   = "0.13.0"
  val Enumeratum              = "1.6.1"
  val GeoTrellis              = "3.6.0"
  val Jts                     = "1.16.1"
  val Monocle                 = "2.1.0"
  val Refined                 = "0.9.24"
  val ScalacheckCats          = "0.3.0"
  val Scalacheck              = "1.15.4"
  val ScalatestPlusScalacheck = "3.2.2.0"
  val Scalatest               = "3.2.8"
  val Scapegoat               = "1.4.8"
  val Shapeless               = "2.3.6"
  val SpdxChecker             = "1.0.0"
  val Sttp                    = "3.2.3"
  val SttpModel               = "1.4.4"
  val SttpShared              = "1.2.4"
  val ThreeTenExtra           = "1.6.0"
}

object Dependencies {

  private def ver(for212: String, for213: String) = Def.setting {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 12)) => for212
      case Some((2, 13)) => for213
      case _             => sys.error("not good")
    }
  }

  def geotrellis(module: String) = Def.setting {
    "org.locationtech.geotrellis" %% s"geotrellis-$module" % ver(Versions.GeoTrellis, "3.6.1-SNAPSHOT").value
  }
}
