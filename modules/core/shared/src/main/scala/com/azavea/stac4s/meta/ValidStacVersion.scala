package com.azavea.stac4s.meta

import eu.timepit.refined.api.Validate

final case class ValidStacVersion()

object ValidStacVersion {

  val stacVersions = List(
    "1.0.0",
    "1.0.0-rc4"
  )

  implicit def validStacVersion: Validate.Plain[String, ValidStacVersion] =
    Validate.fromPredicate(
      (s: String) => stacVersions.contains(s),
      t => s"Invalid STAC Version: $t",
      ValidStacVersion()
    )
}
