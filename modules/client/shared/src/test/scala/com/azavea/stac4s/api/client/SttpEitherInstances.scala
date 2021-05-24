package com.azavea.stac4s.api.client

import cats.effect.{ExitCase, Sync}

trait SttpEitherInstances {

  /** [[Sync]] instance defined for Either[Throwable, *].
    * It is required (sadly) to derive [[fs2.Stream.Compiler]] which is necessary for the [[fs2.Stream.compile]] function.
    */
  implicit val eitherSync: Sync[Either[Throwable, *]] = new Sync[Either[Throwable, *]] {
    lazy val me = cats.instances.either.catsStdInstancesForEither[Throwable]

    def suspend[A](thunk: => Either[Throwable, A]): Either[Throwable, A] = thunk

    def bracketCase[A, B](acquire: Either[Throwable, A])(use: A => Either[Throwable, B])(
        release: (A, ExitCase[Throwable]) => Either[Throwable, Unit]
    ): Either[Throwable, B] =
      flatMap(acquire)(use)

    def flatMap[A, B](fa: Either[Throwable, A])(f: A => Either[Throwable, B]): Either[Throwable, B] =
      fa.flatMap(f)

    def tailRecM[A, B](a: A)(f: A => Either[Throwable, Either[A, B]]): Either[Throwable, B] =
      me.tailRecM(a)(f)

    def raiseError[A](e: Throwable): Either[Throwable, A] = me.raiseError(e)

    def handleErrorWith[A](fa: Either[Throwable, A])(f: Throwable => Either[Throwable, A]): Either[Throwable, A] =
      me.handleErrorWith(fa)(f)

    def pure[A](x: A): Either[Throwable, A] = me.pure(x)
  }
}
