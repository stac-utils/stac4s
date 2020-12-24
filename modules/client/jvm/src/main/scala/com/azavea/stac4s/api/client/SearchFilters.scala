package com.azavea.stac4s.api.client

import com.azavea.stac4s.Bbox
import com.azavea.stac4s.api.client.utils.ClientCodecs
import com.azavea.stac4s.types.TemporalExtent

import alleycats.Empty
import eu.timepit.refined.types.numeric.NonNegInt
import geotrellis.vector.{io => _, _}
import io.circe._
import io.circe.generic.semiauto._
import io.circe.refined._

case class SearchFilters(
    bbox: Option[Bbox] = None,
    datetime: Option[TemporalExtent] = None,
    intersects: Option[Geometry] = None,
    collections: List[String] = Nil,
    items: List[String] = Nil,
    limit: Option[NonNegInt] = None,
    query: Map[String, List[Query]] = Map.empty,
    next: Option[PaginationToken] = None
)

object SearchFilters extends ClientCodecs {

  implicit val searchFiltersEmpty: Empty[SearchFilters] = Empty(SearchFilters())

  implicit val searchFilterDecoder: Decoder[SearchFilters] = { c =>
    for {
      bbox              <- c.downField("bbox").as[Option[Bbox]]
      datetime          <- c.downField("datetime").as[Option[TemporalExtent]]
      intersects        <- c.downField("intersects").as[Option[Geometry]]
      collectionsOption <- c.downField("collections").as[Option[List[String]]]
      itemsOption       <- c.downField("items").as[Option[List[String]]]
      limit             <- c.downField("limit").as[Option[NonNegInt]]
      query             <- c.get[Option[Map[String, List[Query]]]]("query")
      paginationToken   <- c.get[Option[PaginationToken]]("next")
    } yield {
      SearchFilters(
        bbox,
        datetime,
        intersects,
        collectionsOption.getOrElse(Nil),
        itemsOption.getOrElse(Nil),
        limit,
        query getOrElse Map.empty,
        paginationToken
      )
    }
  }

  implicit val searchFilterEncoder: Encoder[SearchFilters] = deriveEncoder
}
