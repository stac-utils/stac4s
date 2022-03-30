package com.azavea.stac4s.api.client

import com.azavea.stac4s.api.client.util.ClientCodecs
import com.azavea.stac4s.{Bbox, TemporalExtent, productFieldNames}

import cats.syntax.option._
import eu.timepit.refined.types.numeric.NonNegInt
import geotrellis.vector.{io => _, _}
import io.circe._
import io.circe.refined._
import io.circe.syntax._

case class SearchFilters(
    bbox: Option[Bbox] = None,
    datetime: Option[TemporalExtent] = None,
    intersects: Option[Geometry] = None,
    collections: List[String] = Nil,
    items: List[String] = Nil,
    limit: Option[NonNegInt] = None,
    query: Map[String, List[Query]] = Map.empty,
    paginationBody: JsonObject = JsonObject.empty
)

object SearchFilters extends ClientCodecs {
  val searchFilterFields = productFieldNames[SearchFilters]

  implicit val searchFiltersDecoder: Decoder[SearchFilters] = { c =>
    for {
      bbox              <- c.downField("bbox").as[Option[Bbox]]
      datetime          <- c.downField("datetime").as[Option[TemporalExtent]]
      intersects        <- c.downField("intersects").as[Option[Geometry]]
      collectionsOption <- c.downField("collections").as[Option[List[String]]]
      itemsOption       <- c.downField("ids").as[Option[List[String]]]
      limit             <- c.downField("limit").as[Option[NonNegInt]]
      query             <- c.get[Option[Map[String, List[Query]]]]("query")
      document          <- c.value.as[JsonObject]
    } yield {
      SearchFilters(
        bbox,
        datetime,
        intersects,
        collectionsOption getOrElse Nil,
        itemsOption getOrElse Nil,
        limit,
        query getOrElse Map.empty,
        document.filter { case (k, _) => !searchFilterFields.contains(k) }
      )
    }
  }

  implicit val searchFiltersEncoder: Encoder[SearchFilters] = { filters =>
    val fieldsEncoder = Encoder.forProduct7(
      "bbox",
      "datetime",
      "intersects",
      "collections",
      "ids",
      "limit",
      "query"
    ) { filters: SearchFilters =>
      (
        filters.bbox,
        filters.datetime,
        filters.intersects,
        filters.collections.some.filter(_.nonEmpty),
        filters.items.some.filter(_.nonEmpty),
        filters.limit,
        filters.query.some.filter(_.nonEmpty)
      )
    }

    fieldsEncoder(filters).deepMerge(filters.paginationBody.asJson)
  }
}
