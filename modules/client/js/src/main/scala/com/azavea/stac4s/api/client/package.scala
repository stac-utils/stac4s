package com.azavea.stac4s.api

package object client {
  type StacClient[F[_]]     = StacClientF[F, SearchFilters]
  type SttpStacClient[F[_]] = SttpStacClientF[F, SearchFilters]
}
