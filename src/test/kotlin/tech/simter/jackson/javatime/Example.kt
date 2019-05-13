package tech.simter.jackson.javatime

import java.time.*
import java.time.temporal.ChronoUnit

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
) {
  companion object {
    private val now = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES)!!
    val instance2minutes = Example(
      name = "",
      localDateTime = now.toLocalDateTime(),
      localDate = now.toLocalDate(),
      localTime = now.toLocalTime(),
      offsetDateTime = now,
      offsetTime = now.toOffsetTime(),
      zonedDateTime = now.toZonedDateTime(),

      instant = now.toInstant(),
      yearMonth = YearMonth.of(now.year, now.monthValue),
      year = Year.of(now.year),
      month = now.month,
      monthDay = MonthDay.of(now.monthValue, now.dayOfMonth)
    )
  }
}