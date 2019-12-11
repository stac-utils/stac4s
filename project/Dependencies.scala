import sbt._

object Versions {
  val CatsVersion           = "1.6.0"
  val CirceFs2Version       = "0.11.0"
  val CirceVersion          = "0.11.1"
  val GeoTrellisVersion     = "3.0.0-M3"
  val RefinedVersion        = "0.9.3"
  val ScapegoatVersion      = "1.3.8"
  val ShapelessVersion      = "2.3.3"
  val spdxCheckerVersion    = "1.0.0"
  val scalacheckCatsVersion = "0.1.1"
  val scalatestVersion      = "3.0.4"
  val sprayVersion          = "1.3.4"
  val scalacheckVersion     = "1.14.0"
}

object Dependencies {
  val cats             = "org.typelevel"               %% "cats-core"           % Versions.CatsVersion
  val circeCore        = "io.circe"                    %% "circe-core"          % Versions.CirceVersion
  val circeFs2         = "io.circe"                    %% "circe-fs2"           % Versions.CirceFs2Version
  val circeGeneric     = "io.circe"                    %% "circe-generic"       % Versions.CirceVersion
  val circeParser      = "io.circe"                    %% "circe-parser"        % Versions.CirceVersion
  val circeRefined     = "io.circe"                    %% "circe-refined"       % Versions.CirceVersion
  val circeShapes      = "io.circe"                    %% "circe-shapes"        % Versions.CirceVersion
  val geotrellisVector = "org.locationtech.geotrellis" %% "geotrellis-vector"   % Versions.GeoTrellisVersion
  val refined          = "eu.timepit"                  %% "refined"             % Versions.RefinedVersion
  val scalacheck       = "org.scalacheck"              %% "scalacheck"          % Versions.scalacheckVersion % Test
  val scalacheckCats   = "io.chrisdavenport"           %% "cats-scalacheck"     % Versions.scalacheckCatsVersion % Test
  val scalatest        = "org.scalatest"               %% "scalatest"           % Versions.scalatestVersion % Test
  val spdxChecker      = "com.github.tbouron"          % "spdx-license-checker" % Versions.spdxCheckerVersion
  val shapeless        = "com.chuusai"                 %% "shapeless"           % Versions.ShapelessVersion
  val spray            = "io.spray"                    %% "spray-json"          % Versions.sprayVersion
}
