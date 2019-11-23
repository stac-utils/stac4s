package com.azavea.stac4s.meta

import com.github.tbouron.SpdxLicense
import eu.timepit.refined.api.Validate

final case class ValidSpdxId()

object ValidSpdxId {

  implicit def validSpdxId: Validate.Plain[String, ValidSpdxId] =
    Validate.fromPredicate(
      SpdxLicense.isValidId,
      t => s"Invalid SPDX Id: $t",
      ValidSpdxId()
    )
}
