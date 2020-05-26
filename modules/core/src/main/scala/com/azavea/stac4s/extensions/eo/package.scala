package com.azavea.stac4s.extensions

import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.numeric._
import eu.timepit.refined.types.numeric.PosInt

package object eo {
  type Percentage = Int Refined Interval.Closed[W.`0`.T, W.`100`.T]
  object Percentage extends RefinedTypeOps[Percentage, Int]

  type BandRange = PosInt
}
