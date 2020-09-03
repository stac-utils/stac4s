package com.azavea.stac4s

import com.azavea.stac4s.syntax._
import com.azavea.stac4s.extensions._
import com.azavea.stac4s.extensions.asset._
import com.azavea.stac4s.extensions.eo._
import com.azavea.stac4s.extensions.label._
import com.azavea.stac4s.testing._
import io.circe.syntax._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.Checkers

class SyntaxSpec extends AnyFunSuite with Checkers with Matchers {
  test("item syntax results in the same values as typeclass summoner to extend") {
    check { (item: StacItem, labelExtension: LabelItemExtension) =>
      item.addExtensionFields(labelExtension) == ItemExtension[LabelItemExtension]
        .addExtensionFields(item, labelExtension)
    }
  }

  test("item syntax results in the same values as typeclass summoner to parse") {
    check { (item: StacItem, labelExtension: LabelItemExtension) =>
      item.addExtensionFields(labelExtension).getExtensionFields[LabelItemExtension] ==
        labelExtension.valid
    }
  }

  test("collection syntax results in the same values as typeclass summoner to extend") {
    check { (collection: StacCollection, assetExtension: AssetCollectionExtension) =>
      collection.addExtensionFields(assetExtension) == CollectionExtension[AssetCollectionExtension]
        .addExtensionFields(collection, assetExtension)
    }
  }

  test("collection syntax results in the same values as typeclass summoner to parse") {
    check { (collection: StacCollection, assetExtension: AssetCollectionExtension) =>
      // We have to nuke existing extensions, because otherwise the collection can start with
      // some assets in its asset extension, in which case the decoded result will have extra
      // data
      val withoutExtensions = collection.copy(extensionFields = ().asJsonObject)
      withoutExtensions
        .addExtensionFields(assetExtension)
        .getExtensionFields[AssetCollectionExtension] == assetExtension.valid
    }
  }

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
    check { (stacItemAsset: StacItemAsset, eoAssetExtension: EOAssetExtension) =>
      stacItemAsset.addExtensionFields(eoAssetExtension) == ItemAssetExtension[EOAssetExtension]
        .addExtensionFields(stacItemAsset, eoAssetExtension)
    }
  }

  test("asset syntax results in the same values as typeclass summoner to parse") {
    check { (stacItemAsset: StacItemAsset, eoAssetExtension: EOAssetExtension) =>
      stacItemAsset.addExtensionFields(eoAssetExtension).getExtensionFields[EOAssetExtension] == eoAssetExtension.valid
    }
  }

}
