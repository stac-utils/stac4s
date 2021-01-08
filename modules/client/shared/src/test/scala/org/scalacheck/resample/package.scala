package org.scalacheck

import scala.annotation.tailrec

package object resample {

  /** https://github.com/typelevel/scalacheck/issues/650#issuecomment-625384900 */
  implicit class GenOps[A](val g: Gen[A]) extends AnyVal {

    def resample(retries: Int = 1000): A = {
      @tailrec
      def loop(tries: Int): A =
        if (tries >= retries) sys.error("Generator failed to produce a non-empty result.")
        else
          g.sample match {
            case Some(a) => a
            case None    => loop(tries + 1)
          }
      loop(0)
    }
  }
}
