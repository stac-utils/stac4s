package com.azavea.stac4s.api.client

import com.azavea.stac4s.api.client.util.ClientCodecs
import com.azavea.stac4s.{Bbox, TemporalExtent}

import eu.timepit.refined.types.numeric.NonNegInt
import geotrellis.vector.{io => _, _}
import io.circe._
import io.circe.refined._
import monocle.Lens
import monocle.macros.GenLens

case class SearchFilters(
    bbox: Option[Bbox] = None,
    datetime: Option[TemporalExtent] = None,
    intersects: Option[Geometry] = None,
    collections: Option[List[String]] = None,
    items: Option[List[String]] = None,
    limit: Option[NonNegInt] = None,
    query: Option[Map[String, List[Query]]] = None,
    next: Option[PaginationToken] = None
)

object SearchFilters extends ClientCodecs {
  implicit val paginationTokenLens: Lens[SearchFilters, Option[PaginationToken]] = GenLens[SearchFilters](_.next)

  implicit val searchFiltersDecoder: Decoder[SearchFilters] = { c =>
    for {
      bbox              <- c.downField("bbox").as[Option[Bbox]]
      datetime          <- c.downField("datetime").as[Option[TemporalExtent]]
      intersects        <- c.downField("intersects").as[Option[Geometry]]
      collectionsOption <- c.downField("collections").as[Option[List[String]]]
      itemsOption       <- c.downField("ids").as[Option[List[String]]]
      limit             <- c.downField("limit").as[Option[NonNegInt]]
      query             <- c.get[Option[Map[String, List[Query]]]]("query")
      paginationToken   <- c.get[Option[PaginationToken]]("next")
    } yield {
      SearchFilters(
        bbox,
        datetime,
        intersects,
        collectionsOption,
        itemsOption,
        limit,
        query,
        paginationToken
      )
    }
  }

  implicit val searchFiltersEncoder: Encoder[SearchFilters] = Encoder.forProduct8(
    "bbox",
    "datetime",
    "intersects",
    "collections",
    "ids",
    "limit",
    "query",
    "next"
  )(filters =>
    (
      filters.bbox,
      filters.datetime,
      filters.intersects,
      filters.collections,
      filters.items,
      filters.limit,
      filters.query,
      filters.next
    )
  )
}
