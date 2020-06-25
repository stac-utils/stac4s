package com.azavea.stac4s.extensions

import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.numeric._
import eu.timepit.refined.types.numeric.PosInt

package object eo {
  type Percentage = Double Refined Interval.Closed[W.`0D`.T, W.`100D`.T]
  object Percentage extends RefinedTypeOps[Percentage, Double]

  type BandRange = PosInt
}
