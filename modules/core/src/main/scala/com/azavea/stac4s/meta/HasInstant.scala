package com.azavea.stac4s.meta

import eu.timepit.refined.api.Validate
import org.joda.time.Instant

final case class HasInstant()

object HasInstant {

  implicit def hasInstant: Validate.Plain[Option[Instant], HasInstant] =
    Validate.fromPredicate(
      {
        case None => false
        case _    => true
      },
      t => s"Value Is None: $t",
      HasInstant()
    )
}
