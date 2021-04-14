package com.azavea.stac4s

import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.generic._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.W

package object types {

  type CatalogType    = String Refined Equal[W.`"Catalog"`.T]
  type CollectionType = String Refined Equal[W.`"Collection"`.T]
}
