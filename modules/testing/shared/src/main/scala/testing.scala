package com.azavea.stac4s

import cats.data.NonEmptyList
import eu.timepit.refined.types.string.NonEmptyString
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen

import java.time.Instant

package object testing {

  def nonEmptyStringGen: Gen[String] =
    Gen.listOfN(30, Gen.alphaChar) map { _.mkString }

  def nonEmptyAlphaRefinedStringGen: Gen[NonEmptyString] =
    nonEmptyStringGen map NonEmptyString.unsafeFrom

  def possiblyEmptyListGen[T](g: Gen[T]) =
    Gen.choose(0, 10) flatMap { count => Gen.listOfN(count, g) }

  def possiblyEmptyMapGen[T, U](g: Gen[(T, U)]) =
    Gen.choose(0, 10) flatMap { count => Gen.mapOfN(count, g) }

  def nonEmptyListGen[T](g: Gen[T]): Gen[NonEmptyList[T]] =
    Gen.nonEmptyListOf(g) map { NonEmptyList.fromListUnsafe }

  def instantGen: Gen[Instant] = arbitrary[Int] map { x => Instant.now.plusMillis(x.toLong) }

}
