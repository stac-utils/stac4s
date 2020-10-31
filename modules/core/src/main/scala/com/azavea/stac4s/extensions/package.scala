package com.azavea.stac4s

import cats.data.ValidatedNel
import io.circe.Error

package object extensions {

  // convenience type not to have to write ValidatedNel in a few places /
  // to expose a nicer API to users (a la MAML:
  // https://github.com/geotrellis/maml/blob/713c6a0c54646d1972855bf5a1f0efddd108f95d/shared/src/main/scala/error/package.scala#L8)
  type ExtensionResult[T] = ValidatedNel[Error, T]

}
