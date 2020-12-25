package com.azavea.stac4s.api

package object client {
  type SttpStacClient[F[_]] = BaseSttpStacClient.Aux[F, SearchFilters]
}
