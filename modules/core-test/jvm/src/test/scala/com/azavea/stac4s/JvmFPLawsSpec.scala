package com.azavea.stac4s

import com.azavea.stac4s.testing.TestInstances

import cats.kernel.laws.discipline.SemigroupTests
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.Checkers
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

class JvmFPLawsSpec extends AnyFunSuite with FunSuiteDiscipline with Checkers with Matchers with TestInstances {
  checkAll("Semigroup.Bbox", SemigroupTests[Bbox].semigroup)
}
