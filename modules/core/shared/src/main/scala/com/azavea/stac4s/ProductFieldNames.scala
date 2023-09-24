package com.azavea.stac4s

import shapeless.ops.hlist.ToTraversable
import shapeless.ops.record.Keys
import shapeless.{HList, LabelledGeneric}

import scala.annotation.nowarn

trait ProductFieldNames[T] {
  def get: Set[String]
}

object ProductFieldNames {
  def apply[T](implicit ev: ProductFieldNames[T]): ProductFieldNames[T] = ev

  @SuppressWarnings(Array("all"))
  implicit def fromLabelledGeneric[T, L <: HList, K <: HList](implicit
      @nowarn lg: LabelledGeneric.Aux[T, L],
      keys: Keys.Aux[L, K],
      toList: ToTraversable.Aux[K, List, Symbol]
  ): ProductFieldNames[T] =
    new ProductFieldNames[T] {
      def get: Set[String] = keys().toList.flatMap(field => substituteFieldName(field.name)).toSet
    }
}
