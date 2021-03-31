package com.azavea.stac4s.extensions.periodic

import com.azavea.stac4s.Interval

import org.threeten.extra.PeriodDuration

final case class PeriodicExtent(
    range: Interval,
    period: PeriodDuration
)
