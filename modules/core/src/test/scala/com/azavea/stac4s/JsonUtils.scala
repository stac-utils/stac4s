package com.azavea.stac4s

import cats.syntax.either._
import io.circe.Json
import io.circe.parser._

object JsonUtils {

  def getJson(resource: String): Json = {
    val stream = getClass.getResourceAsStream(resource)
    val lines  = scala.io.Source.fromInputStream(stream).getLines
    val json   = lines.mkString(" ")
    stream.close()
    parse(json).valueOr(throw _)
  }
}
