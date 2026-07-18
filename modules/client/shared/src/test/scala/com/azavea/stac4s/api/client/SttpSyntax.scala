package com.azavea.stac4s.api.client

import cats.syntax.either._
import cats.syntax.option._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Json
import sttp.client4.Response
import sttp.client4.testing.{ResponseStub, StubBody}
import sttp.model.{Header, HeaderNames, StatusCode}

trait SttpSyntax {

  implicit class ResponseOps(self: Response.type) {

    def empty: Either[Nothing, Response[StubBody]] = ResponseStub.exact("".asRight).asRight

    def item[T](item: T): Either[Nothing, Response[StubBody]] =
      ResponseStub.exact(item.asRight, StatusCode.Ok, Header(HeaderNames.Etag, item.##.toString) :: Nil).asRight

    def json(json: Json): Either[Nothing, Response[StubBody]] =
      ResponseStub.exact(json.asRight).asRight
  }

  implicit class EtagOps[T](val self: T) {
    def etag: ETag[T] = ETag(self, NonEmptyString.unsafeFrom(self.##.toString).some)
  }
}
