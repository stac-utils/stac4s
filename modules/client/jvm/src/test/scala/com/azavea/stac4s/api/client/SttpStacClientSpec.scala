package com.azavea.stac4s.api.client

import com.azavea.stac4s.testing.JvmInstances

import sttp.client3.UriContext

class SttpStacClientSpec extends BaseSttpStacClientSpec with JvmInstances {
  lazy val client = SttpStacClient(backend, uri"http://localhost:9090")
}
