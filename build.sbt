import xerial.sbt.Sonatype._

lazy val commonSettings = Seq(
  // We are overriding the default behavior of sbt-git which, by default, only
  // appends the `-SNAPSHOT` suffix if there are uncommitted changes in the
  // workspace.
  version := {
    if (git.gitDescribedVersion.value.isEmpty)
      git.gitHeadCommit.value.get.substring(0, 7) + "-SNAPSHOT"
    else if (git.gitCurrentTags.value.isEmpty || git.gitUncommittedChanges.value)
      git.gitDescribedVersion.value.get + "-SNAPSHOT"
    else
      git.gitDescribedVersion.value.get
  },
  scalaVersion := "2.12.12",
  cancelable in Global := true,
  scalafmtOnCompile := true,
  scapegoatVersion in ThisBuild := Versions.Scapegoat,
  scapegoatDisabledInspections := Seq("ObjectNames", "EmptyCaseClass"),
  unusedCompileDependenciesFilter -= moduleFilter("com.sksamuel.scapegoat", "scalac-scapegoat-plugin"),
  addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.11.2" cross CrossVersion.full),
  addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
  addCompilerPlugin(
    "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
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
  ),
  scalafixDependencies in ThisBuild += "com.github.liancheng" %% "organize-imports" % "0.4.4"
)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

lazy val publishSettings = Seq(
  organization := "com.azavea.stac4s",
  organizationName := "Azavea",
  organizationHomepage := Some(new URL("https://azavea.com/")),
  description := "stac4s is a scala library with primitives to build applications using the SpatioTemporal Asset Catalogs specification",
  publishArtifact in Test := false
) ++ sonatypeSettings ++ credentialSettings

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

lazy val credentialSettings = Seq(
  credentials ++= List(
    for {
      id <- sys.env.get("GPG_KEY_ID")
    } yield Credentials("GnuPG Key ID", "gpg", id, "ignored"),
    for {
      user <- sys.env.get("SONATYPE_USERNAME")
      pass <- sys.env.get("SONATYPE_PASSWORD")
    } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, pass)
  ).flatten
)

val coreDependenciesJVM = Seq(
  "org.locationtech.jts"         % "jts-core"          % Versions.Jts,
  "org.locationtech.geotrellis" %% "geotrellis-vector" % Versions.GeoTrellis
)

val testingDependenciesJVM = Seq(
  "org.locationtech.geotrellis" %% "geotrellis-vector" % Versions.GeoTrellis,
  "org.locationtech.jts"         % "jts-core"          % Versions.Jts
)

val testRunnerDependenciesJVM = Seq(
  "io.circe"          %% "circe-testing"   % Versions.Circe                   % Test,
  "org.scalatest"     %% "scalatest"       % Versions.Scalatest               % Test,
  "org.scalatestplus" %% "scalacheck-1-14" % Versions.ScalatestPlusScalacheck % Test
)

lazy val root = project
  .in(file("."))
  .settings(moduleName := "root")
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
      "com.beachape"  %%% "enumeratum"       % Versions.Enumeratum,
      "com.beachape"  %%% "enumeratum-circe" % Versions.Enumeratum,
      "com.chuusai"   %%% "shapeless"        % Versions.Shapeless,
      "eu.timepit"    %%% "refined"          % Versions.Refined,
      "io.circe"      %%% "circe-core"       % Versions.Circe,
      "io.circe"      %%% "circe-generic"    % Versions.Circe,
      "io.circe"      %%% "circe-parser"     % Versions.Circe,
      "io.circe"      %%% "circe-refined"    % Versions.Circe,
      "org.typelevel" %%% "cats-core"        % Versions.Cats,
      "org.typelevel" %%% "cats-kernel"      % Versions.Cats
    )
  })
  .jvmSettings(
    libraryDependencies ++= coreDependenciesJVM
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "io.github.cquiroz" %% "scala-java-time" % "2.1.0"
    )
  )

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
      "org.scalacheck"    %%% "scalacheck"            % Versions.Scalacheck,
      "org.typelevel"     %%% "cats-core"             % Versions.Cats
    )
  )
  .jvmSettings(libraryDependencies ++= testingDependenciesJVM)

lazy val testingJVM = testing.jvm
lazy val testingJS  = testing.js

lazy val coreTest = crossProject(JSPlatform, JVMPlatform)
  .in(file("modules/core-test"))
  .dependsOn(testing % Test)
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(
    libraryDependencies ++= Seq(
      "io.circe"          %%% "circe-testing"   % Versions.Circe                   % Test,
      "org.scalatest"     %%% "scalatest"       % Versions.Scalatest               % Test,
      "org.scalatestplus" %%% "scalacheck-1-14" % Versions.ScalatestPlusScalacheck % Test
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "io.github.cquiroz" %%% "scala-java-time" % "2.1.0" % Test
    )
  )

lazy val coreTestJVM = coreTest.jvm
lazy val coreTestJS  = coreTest.js
lazy val coreTestRef = LocalProject("modules/core-test")

lazy val client = crossProject(JSPlatform, JVMPlatform)
  .in(file("modules/client"))
  .dependsOn(core)
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    libraryDependencies ++= Seq(
      "io.circe"                      %% "circe-core"                     % Versions.Circe,
      "io.circe"                      %% "circe-generic"                  % Versions.Circe,
      "io.circe"                      %% "circe-refined"                  % Versions.Circe,
      "com.chuusai"                   %% "shapeless"                      % Versions.Shapeless,
      "eu.timepit"                    %% "refined"                        % Versions.Refined,
      "org.locationtech.geotrellis"   %% "geotrellis-vector"              % Versions.GeoTrellis,
      "org.locationtech.jts"           % "jts-core"                       % Versions.Jts,
      "org.typelevel"                 %% "cats-core"                      % Versions.Cats,
      "com.softwaremill.sttp.client3" %% "core"                           % Versions.Sttp,
      "com.softwaremill.sttp.client3" %% "circe"                          % Versions.Sttp,
      "com.softwaremill.sttp.client3" %% "json-common"                    % Versions.Sttp,
      "com.softwaremill.sttp.model"   %% "core"                           % Versions.SttpModel,
      "com.softwaremill.sttp.shared"  %% "core"                           % Versions.SttpShared,
      "com.softwaremill.sttp.client3" %% "http4s-backend"                 % Versions.Sttp      % Test,
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats" % Versions.Sttp      % Test,
      "org.scalatest"                %%% "scalatest"                      % Versions.Scalatest % Test,
      "io.chrisdavenport"             %% "log4cats-core"                  % Versions.Log4Cats,
      "io.chrisdavenport"             %% "log4cats-slf4j"                 % Versions.Log4Cats  % Test
    )
  )

lazy val clientJVM = client.jvm
lazy val clientJS  = client.js
