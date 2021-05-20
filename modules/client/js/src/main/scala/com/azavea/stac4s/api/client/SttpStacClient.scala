package com.azavea.stac4s.api.client

import cats.MonadThrow
import sttp.client3.SttpBackend
import sttp.model.Uri

object SttpStacClient {

  def apply[F[_]: MonadThrow](client: SttpBackend[F, Any], baseUri: Uri): SttpStacClient[F] =
    SttpStacClientF[F, SearchFilters](client, baseUri)
}
