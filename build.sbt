ThisBuild / versionScheme     := Some("semver-spec")
ThisBuild / semanticdbVersion := "4.13.9"

lazy val commonSettings = Seq(
  scalaVersion                 := "2.13.16",
  crossScalaVersions           := List("2.13.16", "2.12.20"),
  Global / cancelable          := true,
  scalafmtOnCompile            := false,
  ThisBuild / scapegoatVersion := Versions.Scapegoat,
  scapegoatDisabledInspections := Seq("ObjectNames", "EmptyCaseClass"),
  addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.4" cross CrossVersion.full),
  addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
  addCompilerPlugin("org.scalameta"  % "semanticdb-scalac"  % "4.13.10" cross CrossVersion.full),
  autoCompilerPlugins := true,
  externalResolvers   := Seq(DefaultMavenRepository, Resolver.sonatypeCentralSnapshots) ++ Seq(
    Resolver.typesafeIvyRepo("releases"),
    "locationtech-releases" at "https://repo.locationtech.org/content/groups/releases",
    "locationtech-snapshots" at "https://repo.locationtech.org/content/groups/snapshots",
    "jitpack".at("https://jitpack.io"),
    Resolver.file("local", file(Path.userHome.absolutePath + "/.ivy2/local"))(
      Resolver.ivyStylePatterns
    )
  )
)

lazy val noPublishSettings = Seq(
  publish         := {},
  publishLocal    := {},
  publishArtifact := false
)

lazy val publishSettings = Seq(
  organization         := "com.azavea.stac4s",
  organizationName     := "Azavea",
  organizationHomepage := Some(url("https://azavea.com/")),
  description := "stac4s is a scala library with primitives to build applications using the SpatioTemporal Asset Catalogs specification",
  Test / publishArtifact := false
) ++ sonatypeSettings

lazy val sonatypeSettings = Seq(
  publishMavenStyle := true,
  developers        := List(
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
      id = "pomadchin",
      name = "Grigory Pomadchin",
      email = "gpomadchin@azavea.com",
      url = url("https://github.com/pomadchin")
    ),
    Developer(
      id = "azavea",
      name = "Azavea Inc.",
      email = "systems@azavea.com",
      url = url("https://www.azavea.com")
    )
  ),
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
)

val jvmGeometryDependencies = Def.setting {
  Seq(
    "org.locationtech.jts"         % "jts-core"          % Versions.Jts,
    "org.locationtech.geotrellis" %% "geotrellis-vector" % Versions.GeoTrellis
  )
}

val coreDependenciesJVM = Def.setting {
  Seq(
    "org.threeten" % "threeten-extra"    % Versions.ThreeTenExtra,
    "io.circe"    %% "circe-json-schema" % Versions.CirceJsonSchema
  ) ++ jvmGeometryDependencies.value
}

val testingDependenciesJVM = Def.setting {
  Seq(
    "org.locationtech.geotrellis" %% "geotrellis-vector" % Versions.GeoTrellis,
    "org.locationtech.jts"         % "jts-core"          % Versions.Jts,
    "org.threeten"                 % "threeten-extra"    % Versions.ThreeTenExtra
  )
}

val testRunnerDependenciesJVM = Seq(
  "io.circe"          %% "circe-testing"   % Versions.Circe                   % Test,
  "org.scalatest"     %% "scalatest"       % Versions.Scalatest               % Test,
  "org.scalatestplus" %% "scalacheck-1-16" % Versions.ScalatestPlusScalacheck % Test
)

lazy val root = project
  .in(file("."))
  .settings(name := "stac4s")
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(noPublishSettings)
  .aggregate(coreJS, coreJVM, testingJS, testingJVM, coreTestJS, coreTestJVM, clientJS, clientJVM)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .in(file("modules/core"))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings({
    libraryDependencies ++= Seq(
      "com.beachape"               %%% "enumeratum"       % Versions.Enumeratum,
      "com.beachape"               %%% "enumeratum-circe" % Versions.Enumeratum,
      "com.chuusai"                %%% "shapeless"        % Versions.Shapeless,
      "com.github.julien-truffaut" %%% "monocle-core"     % Versions.Monocle,
      "com.github.julien-truffaut" %%% "monocle-macro"    % Versions.Monocle,
      "eu.timepit"                 %%% "refined"          % Versions.Refined,
      "io.circe"                   %%% "circe-core"       % Versions.Circe,
      "io.circe"                   %%% "circe-generic"    % Versions.Circe,
      "io.circe"                   %%% "circe-parser"     % Versions.Circe,
      "io.circe"                   %%% "circe-refined"    % Versions.CirceRefined,
      "org.typelevel"              %%% "cats-core"        % Versions.Cats,
      "org.typelevel"              %%% "cats-kernel"      % Versions.Cats
    )
  })
  .jvmSettings(libraryDependencies ++= coreDependenciesJVM.value)
  .jsSettings(libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % Versions.ScalaJavaTime)

lazy val coreJVM = core.jvm
lazy val coreJS  = core.js

lazy val testing = crossProject(JSPlatform, JVMPlatform)
  .in(file("modules/testing"))
  .dependsOn(core)
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.beachape"      %%% "enumeratum"            % Versions.Enumeratum,
      "com.beachape"      %%% "enumeratum-scalacheck" % Versions.Enumeratum,
      "com.chuusai"       %%% "shapeless"             % Versions.Shapeless,
      "eu.timepit"        %%% "refined-scalacheck"    % Versions.Refined,
      "eu.timepit"        %%% "refined"               % Versions.Refined,
      "io.chrisdavenport" %%% "cats-scalacheck"       % Versions.ScalacheckCats,
      "io.circe"          %%% "circe-core"            % Versions.Circe,
      "io.circe"          %%% "circe-literal"         % Versions.Circe,
      "org.scalacheck"    %%% "scalacheck"            % Versions.Scalacheck,
      "org.typelevel"     %%% "cats-core"             % Versions.Cats
    )
  )
  .jvmSettings(libraryDependencies ++= testingDependenciesJVM.value)
  .jsSettings(libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % Versions.ScalaJavaTime % Test)

lazy val testingJVM = testing.jvm
lazy val testingJS  = testing.js

lazy val coreTest = crossProject(JSPlatform, JVMPlatform)
  .in(file("modules/core-test"))
  .dependsOn(testing % Test)
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(
    libraryDependencies ++= Seq(
      "io.circe"          %%% "circe-testing"        % Versions.Circe                   % Test,
      "org.scalatest"     %%% "scalatest"            % Versions.Scalatest               % Test,
      "org.scalatestplus" %%% "scalacheck-1-16"      % Versions.ScalatestPlusScalacheck % Test,
      "org.typelevel"     %%% "discipline-scalatest" % Versions.DisciplineScalatest     % Test
    )
  )
  .jsSettings(libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % Versions.ScalaJavaTime % Test)

lazy val coreTestJVM = coreTest.jvm
lazy val coreTestJS  = coreTest.js
lazy val coreTestRef = LocalProject("modules/core-test")

lazy val client = crossProject(JSPlatform, JVMPlatform)
  .in(file("modules/client"))
  .dependsOn(core, testing % Test)
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    libraryDependencies ++= Seq(
      "io.circe"                      %%% "circe-core"    % Versions.Circe,
      "io.circe"                      %%% "circe-generic" % Versions.Circe,
      "io.circe"                      %%% "circe-refined" % Versions.CirceRefined,
      "com.chuusai"                   %%% "shapeless"     % Versions.Shapeless,
      "eu.timepit"                    %%% "refined"       % Versions.Refined,
      "org.typelevel"                 %%% "cats-core"     % Versions.Cats,
      "com.softwaremill.sttp.client3" %%% "core"          % Versions.Sttp,
      "com.softwaremill.sttp.client3" %%% "circe"         % Versions.Sttp,
      "com.softwaremill.sttp.client3" %%% "json-common"   % Versions.Sttp,
      "com.softwaremill.sttp.model"   %%% "core"          % Versions.SttpModel,
      "com.softwaremill.sttp.shared"  %%% "core"          % Versions.SttpShared,
      "co.fs2"                        %%% "fs2-core"      % Versions.Fs2,
      "org.scalatest"                 %%% "scalatest"     % Versions.Scalatest % Test
    )
  )
  .jsSettings(libraryDependencies += "io.github.cquiroz" %%% "scala-java-time" % Versions.ScalaJavaTime)
  .jvmSettings(libraryDependencies ++= jvmGeometryDependencies.value)

lazy val clientJVM = client.jvm
lazy val clientJS  = client.js
