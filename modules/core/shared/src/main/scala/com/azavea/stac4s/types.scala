package com.azavea.stac4s

import eu.timepit.refined.W
import eu.timepit.refined.api.Refined
import eu.timepit.refined.generic._

package object types {

  type CatalogType    = String Refined Equal[W.`"Catalog"`.T]
  type CollectionType = String Refined Equal[W.`"Collection"`.T]
}
