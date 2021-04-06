package com.azavea.stac4s.extensions

import com.azavea.stac4s.Interval
import io.circe.Decoder
import io.circe.Encoder
import io.circe.syntax._

trait IntervalExtension[T] {
  def getExtensionFields(interval: Interval): ExtensionResult[T]
  def addExtensionFields(interval: Interval, extensionFields: T): Interval
}

object IntervalExtension {
  def apply[T](implicit ev: IntervalExtension[T]): IntervalExtension[T] = ev

  def instance[T](implicit decoder: Decoder[T], objectEncoder: Encoder.AsObject[T]) =
    new IntervalExtension[T] {

      def getExtensionFields(interval: Interval): ExtensionResult[T] =
        decoder.decodeAccumulating(interval.extensionFields.asJson.hcursor)

      def addExtensionFields(interval: Interval, extensionFields: T): Interval =
        interval.copy(extensionFields = interval.extensionFields.deepMerge(objectEncoder.encodeObject(extensionFields)))
    }
}
