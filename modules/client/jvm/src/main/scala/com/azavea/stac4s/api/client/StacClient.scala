package com.azavea.stac4s.api.client

trait StacClient[F[_]] extends StacClientF[F, SearchFilters]
