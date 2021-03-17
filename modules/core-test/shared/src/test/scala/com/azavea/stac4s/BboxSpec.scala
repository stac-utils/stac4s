package com.azavea.stac4s

import com.azavea.stac4s.testing.TestInstances._

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.Checkers

class BboxSpec extends AnyFunSuite with Checkers with Matchers {

  test("union with left consituent and union is union") {
    check { (bbox1: Bbox, bbox2: Bbox) =>
      bbox1.union(bbox2).union(bbox1) == bbox1.union(bbox2)
    }
  }

  test("union with right constituent and union is union") {
    check { (bbox1: Bbox, bbox2: Bbox) =>
      bbox1.union(bbox2).union(bbox2) == bbox1.union(bbox2)
    }
  }

  test("union with self is union") {
    check { (bbox1: Bbox, bbox2: Bbox) =>
      {
        val unioned = bbox1.union(bbox2)
        unioned.union(unioned) == unioned
      }
    }
  }

}
