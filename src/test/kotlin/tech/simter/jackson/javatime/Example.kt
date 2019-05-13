package tech.simter.jackson.javatime

import java.time.*

data class Example(
  val id: Int? = null,
  val name: String? = null,
  val localDateTime: LocalDateTime? = null,
  val localDate: LocalDate? = null,
  val localTime: LocalTime? = null,
  val offsetDateTime: OffsetDateTime? = null,
  val offsetTime: OffsetTime? = null,
  val zonedDateTime: ZonedDateTime? = null,

  val instant: Instant? = null,
  val yearMonth: YearMonth? = null,
  val year: Year? = null,
  val month: Month? = null,
  val monthDay: MonthDay? = null
)