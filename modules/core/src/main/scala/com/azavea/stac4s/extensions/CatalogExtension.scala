package com.azavea.stac4s.extensions

import com.azavea.stac4s.StacCatalog

import io.circe.{Decoder, Encoder}
import io.circe.syntax._

trait CatalogExtension[T] {
  def getProperties(catalog: StacCatalog): ExtensionResult[T]
  def extend(catalog: StacCatalog, extensionFields: T): StacCatalog
}

object CatalogExtension {
  def apply[T](implicit ev: CatalogExtension[T]): CatalogExtension[T] = ev

  def instance[T](implicit decoder: Decoder[T], objectEncoder: Encoder.AsObject[T]) =
    new CatalogExtension[T] {

      def getProperties(catalog: StacCatalog): ExtensionResult[T] =
        decoder.decodeAccumulating(catalog.extensionFields.asJson.hcursor)

      def extend(catalog: StacCatalog, extensionFields: T): StacCatalog =
        catalog.copy(extensionFields = catalog.extensionFields.deepMerge(objectEncoder.encodeObject(extensionFields)))
    }
}
