package com.azavea.stac4s.api.client

import com.azavea.stac4s.api.client.util.ClientCodecs
import com.azavea.stac4s.geometry.Geometry
import com.azavea.stac4s.{Bbox, TemporalExtent, productFieldNames}

import cats.syntax.option._
import eu.timepit.refined.types.numeric.NonNegInt
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
    // according to the STAC Spec, any fields can be used to represent pagination
    // for more details see https://github.com/radiantearth/stac-api-spec/tree/v1.0.0-rc.1/item-search#pagination
    paginationBody: JsonObject = JsonObject.empty
)

object SearchFilters extends ClientCodecs {
  val searchFilterFields = productFieldNames[SearchFilters]

  implicit val searchFiltersDecoder: Decoder[SearchFilters] = { c =>
    for {
      bbox              <- c.get[Option[Bbox]]("bbox")
      datetime          <- c.get[Option[TemporalExtent]]("datetime")
      intersects        <- c.get[Option[Geometry]]("intersects")
      collectionsOption <- c.get[Option[List[String]]]("collections")
      itemsOption       <- c.get[Option[List[String]]]("ids")
      limit             <- c.get[Option[NonNegInt]]("limit")
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
