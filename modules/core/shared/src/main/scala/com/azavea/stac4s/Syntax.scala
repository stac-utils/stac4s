package com.azavea.stac4s

import com.azavea.stac4s.extensions._

trait Syntax {

  implicit class stacItemExtensions(item: StacItem) {

    def getExtensionFields[T](implicit ev: ItemExtension[T]): ExtensionResult[T] =
      ev.getExtensionFields(item)

    def addExtensionFields[T](properties: T)(implicit ev: ItemExtension[T]): StacItem =
      ev.addExtensionFields(item, properties)
  }

  implicit class stacCatalogExtensions(catalog: StacCatalog) {

    def getExtensionFields[T](implicit ev: CatalogExtension[T]): ExtensionResult[T] =
      ev.getExtensionFields(catalog)

    def addExtensionFields[T](properties: T)(implicit ev: CatalogExtension[T]): StacCatalog =
      ev.addExtensionFields(catalog, properties)
  }

  implicit class StacAssetExtensions(StacAsset: StacAsset) {

    def getExtensionFields[T](implicit ev: StacAssetExtension[T]): ExtensionResult[T] =
      ev.getExtensionFields(StacAsset)

    def addExtensionFields[T](properties: T)(implicit ev: StacAssetExtension[T]): StacAsset =
      ev.addExtensionFields(StacAsset, properties)
  }

  implicit class stacLinkExtensions(link: StacLink) {

    def getExtensionFields[T](implicit ev: LinkExtension[T]): ExtensionResult[T] =
      ev.getExtensionFields(link)

    def addExtensionFields[T](properties: T)(implicit ev: LinkExtension[T]): StacLink =
      ev.addExtensionFields(link, properties)
  }

  implicit class stacItemCollectionExtensions(itemCollection: ItemCollection) {

    def getExtensionFields[T](implicit ev: ItemCollectionExtension[T]): ExtensionResult[T] =
      ev.getExtensionFields(itemCollection)

    def addExtensionFields[T](properties: T)(implicit ev: ItemCollectionExtension[T]): ItemCollection =
      ev.addExtensionFields(itemCollection, properties)
  }
}
