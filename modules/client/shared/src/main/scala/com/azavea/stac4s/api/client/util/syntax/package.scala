package com.azavea.stac4s.api.client.util

import com.azavea.stac4s.api.client.ETag

import eu.timepit.refined.types.string.NonEmptyString
import sttp.client4.{Request, Response}
import sttp.model.HeaderNames

package object syntax {

  implicit class RequestOps[T](val self: Request[T]) extends AnyVal {
    def header(k: String, v: Option[String]): Request[T] = v.fold(self)(self.header(k, _))

    @SuppressWarnings(Array("UnusedMethodParameter"))
    def header(k: String, v: Option[NonEmptyString])(implicit d: DummyImplicit): Request[T] =
      v.fold(self)(e => self.header(k, e.value))

    def headerIfMatch(v: Option[NonEmptyString]): Request[T] = header(HeaderNames.IfMatch, v)
    def headerETag(v: Option[NonEmptyString]): Request[T]    = header(HeaderNames.Etag, v)
  }

  implicit class ResponseOps[T](val self: Response[T]) extends AnyVal {
    def headerETag: Option[NonEmptyString] = self.header(HeaderNames.Etag).flatMap(NonEmptyString.from(_).toOption)
  }

  implicit class ResponseEitherOps[E, T](val self: Response[Either[E, T]]) extends AnyVal {
    def bodyETag: Either[E, ETag[T]] = self.body.map(ETag(_, self.headerETag))
  }
}
