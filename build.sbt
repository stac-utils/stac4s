import xerial.sbt.Sonatype._

cancelable in Global := true
onLoad in Global ~= (_ andThen ("project core" :: _))

lazy val credentialSettings = Seq(
  credentials += Credentials(
    "GnuPG Key ID",
    "gpg",
    System.getenv().get("GPG_KEY_ID"),
    "ignored"
  ),
  credentials += Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    System.getenv().get("SONATYPE_USERNAME"),
    System.getenv().get("SONATYPE_PASSWORD")
  )
)

lazy val sonatypeSettings = Seq(
  publishMavenStyle := true,
  sonatypeProfileName := "com.azavea",
  sonatypeProjectHosting := Some(GitHubHosting(user = "azavea", repository = "stac4s", email = "systems@azavea.com")),
  developers := List(
    Developer(
      id = "cbrown",
      name = "Christopher Brown",
      email = "cbrown@azavea.com",
      url = url("https://github.com/notthatbreezy")
    ),
    Developer(
      id = "jsantucci",
      name = "James Santucci",
      email = "jsantucci@azavea.com",
      url = url("https://github.com/jisantuc")
    ),
    Developer(
      id = "aaronxsu",
      name = "Aaron Su",
      email = "asu@azavea.com",
      url = url("https://github.com/aaronxsu")
    ),
    Developer(
      id = "azavea",
      name = "Azavea Inc.",
      email = "systems@azavea.com",
      url = url("https://www.azavea.com")
    )
  ),
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  publishTo := sonatypePublishTo.value
)

lazy val publishSettings = Seq(
  organization := "com.azavea.stac4s",
  organizationName := "Azavea",
  organizationHomepage := Some(new URL("https://azavea.com/")),
  description := "stac4s is a scala library with primitives to build applications using the SpatioTemporal Asset Catalogs specification",
  publishArtifact in Test := false
) ++ sonatypeSettings ++ credentialSettings

// Versions
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

// Dependencies
val cats             = "org.typelevel"               %% "cats-core"           % CatsVersion
val circeCore        = "io.circe"                    %% "circe-core"          % CirceVersion
val circeFs2         = "io.circe"                    %% "circe-fs2"           % CirceFs2Version
val circeGeneric     = "io.circe"                    %% "circe-generic"       % CirceVersion
val circeParser      = "io.circe"                    %% "circe-parser"        % CirceVersion
val circeRefined     = "io.circe"                    %% "circe-refined"       % CirceVersion
val circeShapes      = "io.circe"                    %% "circe-shapes"        % CirceVersion
val geotrellisVector = "org.locationtech.geotrellis" %% "geotrellis-vector"   % GeoTrellisVersion
val refined          = "eu.timepit"                  %% "refined"             % RefinedVersion
val scalacheck       = "org.scalacheck"              %% "scalacheck"          % scalacheckVersion % Test
val scalacheckCats   = "io.chrisdavenport"           %% "cats-scalacheck"     % scalacheckCatsVersion % Test
val scalatest        = "org.scalatest"               %% "scalatest"           % scalatestVersion % Test
val spdxChecker      = "com.github.tbouron"          % "spdx-license-checker" % spdxCheckerVersion
val shapeless        = "com.chuusai"                 %% "shapeless"           % ShapelessVersion
val spray            = "io.spray"                    %% "spray-json"          % sprayVersion

lazy val settings = Seq(
  organization := "com.azavea",
  name := "stac4s",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.10",
  scalafmtOnCompile := true,
  scapegoatVersion in ThisBuild := Versions.ScapegoatVersion,
  scapegoatDisabledInspections := Seq("ObjectNames", "EmptyCaseClass"),
  unusedCompileDependenciesFilter -= moduleFilter("com.sksamuel.scapegoat", "scalac-scapegoat-plugin"),
  addCompilerPlugin("org.spire-math" %% "kind-projector"     % "0.9.6"),
  addCompilerPlugin("com.olegpy"     %% "better-monadic-for" % "0.2.4"),
  addCompilerPlugin(
    "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full
  ),
  addCompilerPlugin(scalafixSemanticdb),
  autoCompilerPlugins := true,
  externalResolvers := Seq(
    DefaultMavenRepository,
    Resolver.sonatypeRepo("snapshots"),
    Resolver.typesafeIvyRepo("releases"),
    Resolver.bintrayRepo("azavea", "maven"),
    Resolver.bintrayRepo("azavea", "geotrellis"),
    "locationtech-releases" at "https://repo.locationtech.org/content/groups/releases",
    "locationtech-snapshots" at "https://repo.locationtech.org/content/groups/snapshots",
    Resolver.bintrayRepo("guizmaii", "maven"),
    Resolver.bintrayRepo("colisweb", "maven"),
    "jitpack".at("https://jitpack.io"),
    Resolver.file("local", file(Path.userHome.absolutePath + "/.ivy2/local"))(
      Resolver.ivyStylePatterns
    )
  )
)

lazy val dependencies = Seq(
  cats,
  circeCore,
  circeGeneric,
  circeParser,
  refined,
  shapeless,
  scalacheck,
  scalacheckCats,
  scalatest,
  spdxChecker,
  spray,
  geotrellisVector
)

lazy val core = (project in file("modules/core"))
  .settings(settings: _*)
  .settings({
    libraryDependencies ++= dependencies
  })
lazy val coreRef = LocalProject("modules/core")
