package com.azavea.stac4s.api.client

import com.azavea.stac4s.api.client.util.ClientCodecs
import com.azavea.stac4s.{Bbox, TemporalExtent}
import SttpStacClientF.PaginationToken

import cats.syntax.option._
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
    collections: List[String] = Nil,
    items: List[String] = Nil,
    limit: Option[NonNegInt] = None,
    query: Map[String, List[Query]] = Map.empty,
    next: Option[PaginationToken] = None,
    merge: Boolean = false
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
      merge             <- c.get[Option[Boolean]]("merge")
    } yield {
      SearchFilters(
        bbox,
        datetime,
        intersects,
        collectionsOption getOrElse Nil,
        itemsOption getOrElse Nil,
        limit,
        query getOrElse Map.empty,
        paginationToken,
        merge getOrElse false
      )
    }
  }

  implicit val searchFiltersEncoder: Encoder[SearchFilters] = Encoder.forProduct9(
    "bbox",
    "datetime",
    "intersects",
    "collections",
    "ids",
    "limit",
    "query",
    "next",
    "merge"
  )(filters =>
    (
      filters.bbox,
      filters.datetime,
      filters.intersects,
      filters.collections.some.filter(_.nonEmpty),
      filters.items.some.filter(_.nonEmpty),
      filters.limit,
      filters.query.some.filter(_.nonEmpty),
      filters.next,
      filters.merge
    )
  )
}
