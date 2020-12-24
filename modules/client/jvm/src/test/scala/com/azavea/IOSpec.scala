package com.azavea

import cats.effect.{ContextShift, IO, Timer}
import org.scalatest.funspec.AsyncFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{Assertion, Assertions}

trait IOSpec extends AsyncFunSpec with Assertions with Matchers {
  implicit val contextShift: ContextShift[IO] = IO.contextShift(executionContext)
  implicit val timer: Timer[IO]               = IO.timer(executionContext)

  private val itWord = new ItWord

  def it(name: String)(test: => IO[Assertion]): Unit = itWord.apply(name)(test.unsafeToFuture())

  def ignore(name: String)(test: => IO[Assertion]): Unit = super.ignore(name)(test.unsafeToFuture())

  def describe(description: String)(fun: => Unit): Unit = super.describe(description)(fun)
}
