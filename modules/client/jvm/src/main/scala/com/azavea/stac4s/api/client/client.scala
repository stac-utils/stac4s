package com.azavea.stac4s.api

package object client {
  type SttpStacClient[F[_]] = SttpStacClientF.Aux[F, SearchFilters]
}
