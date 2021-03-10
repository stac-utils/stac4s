package com.azavea.stac4s

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.Checkers
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

import cats.kernel.laws.discipline.SemigroupTests
import com.azavea.stac4s.testing.TestInstances

class JsFPLawsSpec extends AnyFunSuite with FunSuiteDiscipline with Checkers with Matchers with TestInstances {
  checkAll("Semigroup.Bbox", SemigroupTests[Bbox].semigroup)
}
