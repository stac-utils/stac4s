package com.azavea.stac4s

import com.azavea.stac4s.extensions._
import com.azavea.stac4s.extensions.eo._
import com.azavea.stac4s.extensions.label._
import com.azavea.stac4s.syntax._
import com.azavea.stac4s.testing.TestInstances._

import cats.syntax.validated._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.Checkers

class SyntaxSpec extends AnyFunSuite with Checkers with Matchers {

  test("link syntax results in the same values as typeclass summoner to extend") {
    check { (stacLink: StacLink, labelLinkExtension: LabelLinkExtension) =>
      stacLink.addExtensionFields(labelLinkExtension) == LinkExtension[LabelLinkExtension]
        .addExtensionFields(stacLink, labelLinkExtension)
    }
  }

  test("link syntax results in the same values as typeclass summoner to parse") {
    check { (stacLink: StacLink, labelLinkExtension: LabelLinkExtension) =>
      stacLink.addExtensionFields(labelLinkExtension).getExtensionFields[LabelLinkExtension] == labelLinkExtension.valid
    }
  }

  test("asset syntax results in the same values as typeclass summoner to extend") {
    check { (ItemAsset: ItemAsset, eoAssetExtension: EOAssetExtension) =>
      ItemAsset.addExtensionFields(eoAssetExtension) == ItemAssetExtension[EOAssetExtension]
        .addExtensionFields(ItemAsset, eoAssetExtension)
    }
  }

  test("asset syntax results in the same values as typeclass summoner to parse") {
    check { (ItemAsset: ItemAsset, eoAssetExtension: EOAssetExtension) =>
      ItemAsset.addExtensionFields(eoAssetExtension).getExtensionFields[EOAssetExtension] == eoAssetExtension.valid
    }
  }

}
