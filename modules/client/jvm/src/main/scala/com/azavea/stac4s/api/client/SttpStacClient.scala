package com.azavea.stac4s.api.client

import cats.MonadError
import sttp.client3.SttpBackend
import sttp.model.Uri

object SttpStacClient {

  def apply[F[_]: MonadError[*[_], Throwable]](
      client: SttpBackend[F, Any],
      baseUri: Uri
  ): BaseSttpStacClient.Aux[F, SearchFilters] =
    BaseSttpStacClient.instance[F, SearchFilters](client, baseUri)
}
