package com.azavea.stac4s.api.client

import cats.effect.kernel.{CancelScope, Poll}
import cats.effect.Sync
import cats.syntax.apply._

import scala.concurrent.duration.{DurationInt, FiniteDuration}

trait SttpEitherInstances {

  private[this] val IdPoll = new Poll[Either[Throwable, *]] {
    def apply[A](fa: Either[Throwable, A]): Either[Throwable, A] = fa
  }

  /** [[Sync]] instance defined for Either[Throwable, *]. It is required (sadly) to derive [[fs2.Stream.Compiler]] which
    * is necessary for the [[fs2.Stream.compile]] function.
    */
  implicit val eitherSync: Sync[Either[Throwable, *]] = new Sync[Either[Throwable, *]] {
    lazy val me = cats.instances.either.catsStdInstancesForEither[Throwable]

    def flatMap[A, B](fa: Either[Throwable, A])(f: A => Either[Throwable, B]): Either[Throwable, B] =
      fa.flatMap(f)

    def tailRecM[A, B](a: A)(f: A => Either[Throwable, Either[A, B]]): Either[Throwable, B] =
      me.tailRecM(a)(f)

    def raiseError[A](e: Throwable): Either[Throwable, A] = me.raiseError(e)

    def handleErrorWith[A](fa: Either[Throwable, A])(f: Throwable => Either[Throwable, A]): Either[Throwable, A] =
      me.handleErrorWith(fa)(f)

    def pure[A](x: A): Either[Throwable, A] = me.pure(x)

    def suspend[A](hint: Sync.Type)(thunk: => A): Either[Throwable, A] = Right(thunk)

    def monotonic: Either[Throwable, FiniteDuration] = Right(1.second)

    def realTime: Either[Throwable, FiniteDuration] = Right(1.second)

    def rootCancelScope: CancelScope = CancelScope.Uncancelable

    def forceR[A, B](fa: Either[Throwable, A])(fb: Either[Throwable, B]): Either[Throwable, B] =
      fa.productR(fb)

    def uncancelable[A](body: Poll[Either[Throwable, *]] => Either[Throwable, A]): Either[Throwable, A] =
      body(IdPoll)

    def canceled: Either[Throwable, Unit] = Right(())

    def onCancel[A](fa: Either[Throwable, A], fin: Either[Throwable, Unit]): Either[Throwable, A] = fa
  }
}
