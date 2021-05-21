package com.azavea.stac4s.api

package object client {
  type SttpStacClient[F[_]]            = SttpStacClientF[F, SearchFilters]
  type StacClient[F[_]]                = StacClientF[F, SearchFilters]
  type StreamingStacClientFS2[F[_]]    = StreamingStacClientF[F, fs2.Stream[F, *], SearchFilters]
  type StreamingStacClient[F[_], G[_]] = StreamingStacClientF[F, G, SearchFilters]
}
