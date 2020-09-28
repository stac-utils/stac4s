package com.azavea.stac4s.meta

import java.time.ZonedDateTime

import eu.timepit.refined.api.Validate

final case class HasInstant()

object HasInstant {

  implicit def hasInstant: Validate.Plain[Option[ZonedDateTime], HasInstant] =
    Validate.fromPredicate(
      {
        case None => false
        case _    => true
      },
      t => s"Value Is None: $t",
      HasInstant()
    )
}
