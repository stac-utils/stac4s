package com.azavea.stac4s

import com.azavea.stac4s.meta.HasInstant

import cats.Eq
import eu.timepit.refined._
import eu.timepit.refined.api._

import eu.timepit.refined.boolean._
import eu.timepit.refined.collection.{Exists, MinSize, _}

import java.time.Instant

package object types {

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

  implicit val eqTemporalExtent: Eq[TemporalExtent] = Eq.fromUniversalEquals
}
