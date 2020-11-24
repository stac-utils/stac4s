package com.azavea

import com.azavea.stac4s.meta.ValidStacVersion

import eu.timepit.refined.api.{Refined, RefinedTypeOps}

package object stac4s {

  type StacVersion = String Refined ValidStacVersion
  object StacVersion extends RefinedTypeOps[StacVersion, String]

  def substituteFieldName(fieldName: String): Option[String] = fieldName match {
    case "_type"           => Some("type")
    case "stacVersion"     => Some("stac_version")
    case "stacExtensions"  => Some("stac_extensions")
    case "extensionFields" => None
    case s                 => Some(s)
  }

  def productFieldNames[T: ProductFieldNames]: Set[String] = ProductFieldNames[T].get
}
