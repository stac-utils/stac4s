package com.azavea.stac4s.api.client

import com.azavea.stac4s.testing.JsInstances

import sttp.client3.UriContext

class SttpStacClientSpec extends SttpStacClientFSpec with JsInstances {
  lazy val client = SttpStacClient(backend, uri"http://localhost:9090")
}
