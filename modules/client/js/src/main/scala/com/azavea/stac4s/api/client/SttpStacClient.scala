package com.azavea.stac4s.api.client

import cats.MonadThrow
import sttp.client4.Backend
import sttp.model.Uri

object SttpStacClient {

  def apply[F[_]: MonadThrow](client: Backend[F], baseUri: Uri): SttpStacClient[F] =
    SttpStacClientF[F, SearchFilters](client, baseUri)
}
