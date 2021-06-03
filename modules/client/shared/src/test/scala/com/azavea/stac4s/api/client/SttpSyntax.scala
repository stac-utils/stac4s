package com.azavea.stac4s.api.client

import cats.syntax.either._
import cats.syntax.option._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Json
import sttp.client3.Response
import sttp.model.{Header, HeaderNames, StatusCode}

trait SttpSyntax {

  implicit class ResponseOps(self: Response.type) {

    def empty: Either[Nothing, Response[Either[Nothing, String]]] = Response.ok("".asRight).asRight

    def item[T](item: T): Either[Nothing, Response[Either[Nothing, T]]] =
      Response(item.asRight, StatusCode.Ok, "OK", Header(HeaderNames.Etag, item.##.toString) :: Nil).asRight

    def json(json: Json): Either[Nothing, Response[Either[Nothing, Json]]] =
      Response.ok(json.asRight).asRight
  }

  implicit class EtagOps[T](val self: T) {
    def etag: ETag[T] = ETag(self, NonEmptyString.unsafeFrom(self.##.toString).some)
  }
}
