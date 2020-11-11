package com.azavea.stac4s

import com.azavea.stac4s.extensions._

package object syntax extends Syntax {

  implicit class stacCollectionExtensions(collection: StacCollection) {

    def getExtensionFields[T](implicit ev: CollectionExtension[T]): ExtensionResult[T] =
      ev.getExtensionFields(collection)

    def addExtensionFields[T](properties: T)(implicit ev: CollectionExtension[T]): StacCollection =
      ev.addExtensionFields(collection, properties)
  }

}
