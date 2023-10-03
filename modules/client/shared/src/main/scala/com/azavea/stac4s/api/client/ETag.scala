package com.azavea.stac4s.api.client

import eu.timepit.refined.types.string.NonEmptyString

case class ETag[T](entity: T, tag: Option[NonEmptyString])

object ETag extends ETagCodecs
