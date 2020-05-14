package com.azavea.stac4s.extensions

import com.azavea.stac4s.StacLink

import io.circe.{Decoder, Encoder}
import io.circe.syntax._

trait LinkExtension[T] {
  def getProperties(link: StacLink): ExtensionResult[T]

  def extend(link: StacLink, extensionFields: T): StacLink
}

object LinkExtension {
  def apply[T](implicit ev: LinkExtension[T]): LinkExtension[T] = ev

  def instance[T](implicit decoder: Decoder[T], objectEncoder: Encoder.AsObject[T]) =
    new LinkExtension[T] {

      def getProperties(link: StacLink): ExtensionResult[T] =
        decoder.decodeAccumulating(link.extensionFields.asJson.hcursor)

      def extend(link: StacLink, extensionFields: T): StacLink =
        link.copy(extensionFields = link.extensionFields.deepMerge(objectEncoder.encodeObject(extensionFields)))
    }
}
