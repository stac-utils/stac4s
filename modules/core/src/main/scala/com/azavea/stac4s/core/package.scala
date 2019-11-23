package com.azavea.stac4s

import java.time.Instant

import com.azavea.stac4s.meta.{HasInstant, ValidSpdxId}
import eu.timepit.refined._
import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.boolean._
import eu.timepit.refined.collection.{Exists, MinSize, _}
import geotrellis.vector.{io => _}

package object core {

  type SpdxId = String Refined ValidSpdxId
  object SpdxId extends RefinedTypeOps[SpdxId, String]

  type TemporalExtent =
    List[Option[Instant]] Refined And[
      And[MinSize[W.`2`.T], MaxSize[W.`2`.T]],
      Exists[HasInstant]
    ]

  object TemporalExtent extends RefinedTypeOps[TemporalExtent, List[Option[Instant]]] {

    def apply(start: Instant, end: Option[Instant]): TemporalExtent =
      TemporalExtent.unsafeFrom(List(Some(start), end))

    def apply(start: Option[Instant], end: Instant): TemporalExtent =
      TemporalExtent.unsafeFrom(List(start, Some(end)))

    def apply(start: Instant, end: Instant): TemporalExtent =
      TemporalExtent.unsafeFrom(List(Some(start), Some(end)))

  }
}
