package com.azavea.stac4s.extensions.label

import com.azavea.stac4s.extensions.ItemExtension

import cats.Eq
import cats.syntax.apply._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor, Json}

case class LabelItemExtension(
    properties: LabelProperties,
    classes: List[LabelClass],
    description: String,
    _type: LabelType,
    tasks: List[LabelTask],
    methods: List[LabelMethod],
    overviews: List[LabelOverview]
)

object LabelItemExtension {

  implicit val encLabelExtensionPropertiesObject: Encoder.AsObject[LabelItemExtension] = Encoder
    .AsObject[Map[String, Json]]
    .contramapObject((properties: LabelItemExtension) =>
      Map(
        "label:properties"  -> properties.properties.asJson,
        "label:classes"     -> properties.classes.asJson,
        "label:description" -> properties.description.asJson,
        "label:type"        -> properties._type.asJson,
        "label:tasks"       -> properties.tasks.asJson,
        "label:methods"     -> properties.methods.asJson,
        "label:overviews"   -> properties.overviews.asJson
      )
    )

  implicit val decLabelExtensionProperties: Decoder[LabelItemExtension] = new Decoder[LabelItemExtension] {

    def apply(c: HCursor) =
      (
        c.downField("label:properties").as[LabelProperties],
        c.downField("label:classes").as[List[LabelClass]],
        c.downField("label:description").as[String],
        c.downField("label:type").as[LabelType],
        c.downField("label:tasks").as[Option[List[LabelTask]]],
        c.downField("label:methods").as[Option[List[LabelMethod]]],
        c.downField("label:overviews").as[Option[List[LabelOverview]]]
      ).mapN(
        (
            properties: LabelProperties,
            classes: List[LabelClass],
            description: String,
            _type: LabelType,
            tasks: Option[List[LabelTask]],
            methods: Option[List[LabelMethod]],
            overviews: Option[List[LabelOverview]]
        ) =>
          LabelItemExtension(
            properties,
            classes,
            description,
            _type,
            tasks getOrElse Nil,
            methods getOrElse Nil,
            overviews getOrElse Nil
          )
      )
  }

  implicit val eqLabelExtensionProperties: Eq[LabelItemExtension] = Eq.fromUniversalEquals

  implicit val itemExtensionLabelProperties: ItemExtension[LabelItemExtension] = ItemExtension.instance
}
