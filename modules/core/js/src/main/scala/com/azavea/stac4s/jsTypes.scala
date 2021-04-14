package com.azavea.stac4s

import com.azavea.stac4s.meta.HasInstant

import cats.Eq
import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.boolean._
import eu.timepit.refined.collection.{Exists, MinSize, _}
import eu.timepit.refined.auto._
import eu.timepit.refined.generic._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.W

import java.time.Instant

package object jsTypes {

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

  type CatalogType    = String Refined Equal[W.`"Catalog"`.T]
  type CollectionType = String Refined Equal[W.`"Collection"`.T]
}
