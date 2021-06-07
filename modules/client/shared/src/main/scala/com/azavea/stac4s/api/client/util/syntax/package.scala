package com.azavea.stac4s.api.client.util

import com.azavea.stac4s.api.client.ETag

import eu.timepit.refined.types.string.NonEmptyString
import sttp.client3.{RequestT, Response}
import sttp.model.HeaderNames

package object syntax {

  implicit class RequestTOps[U[_], T, -R](val self: RequestT[U, T, R]) extends AnyVal {
    def header(k: String, v: Option[String]): RequestT[U, T, R] = v.fold(self)(self.header(k, _))

    @SuppressWarnings(Array("UnusedMethodParameter"))
    def header(k: String, v: Option[NonEmptyString])(implicit d: DummyImplicit): RequestT[U, T, R] =
      v.fold(self)(e => self.header(k, e.value))

    def headerIfMatch(v: Option[NonEmptyString]): RequestT[U, T, R] = header(HeaderNames.IfMatch, v)
    def headerETag(v: Option[NonEmptyString]): RequestT[U, T, R]    = header(HeaderNames.Etag, v)
  }

  implicit class ResponseOps[T](val self: Response[T]) extends AnyVal {
    def headerETag: Option[NonEmptyString] = self.header(HeaderNames.Etag).flatMap(NonEmptyString.from(_).toOption)
  }

  implicit class ResponseEitherOps[E, T](val self: Response[Either[E, T]]) extends AnyVal {
    def bodyETag: Either[E, ETag[T]] = self.body.map(ETag(_, self.headerETag))
  }
}
