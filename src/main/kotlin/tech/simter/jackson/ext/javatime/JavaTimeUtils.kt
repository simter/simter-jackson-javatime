package tech.simter.jackson.ext.javatime

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import kotlin.reflect.KClass

/**
 * @author RJ
 */
object JavaTimeUtils {
  var LOCAL_OFFSET: ZoneOffset = OffsetDateTime.now().offset!!
  var LOCAL_DATE_PATTERN: String = "yyyy-MM-dd"
  var LOCAL_TIME_PATTERN: String = "HH:mm"
  var LOCAL_DATE_TIME_PATTERN: String = "${LOCAL_DATE_PATTERN} ${LOCAL_TIME_PATTERN}"
  var LOCAL_MONTH_DAY_PATTERN: String = "MM-dd"
  private const val DEFAULT_FORMATTER_KEY = "DEFAULT"
  private val CACHE_PATTERNS = mutableMapOf<String, DateTimeFormatter>(
    DEFAULT_FORMATTER_KEY to DateTimeFormatter.ISO_OFFSET_DATE_TIME
  )
  var DEFAULT_LOCAL_PATTERNS = mapOf<KClass<out TemporalAccessor>, String>(
    LocalDateTime::class to LOCAL_DATE_TIME_PATTERN,
    LocalDate::class to LOCAL_DATE_PATTERN,
    LocalTime::class to LOCAL_TIME_PATTERN,
    MonthDay::class to LOCAL_MONTH_DAY_PATTERN,
    OffsetDateTime::class to LOCAL_DATE_TIME_PATTERN,
    OffsetTime::class to LOCAL_TIME_PATTERN,
    ZonedDateTime::class to LOCAL_DATE_TIME_PATTERN
  )

  fun <T : TemporalAccessor> getFormatter(clazz: KClass<out T>, pattern: String? = null): DateTimeFormatter {
    return if (pattern == null || pattern.isEmpty()) {
      getFormatter(clazz = clazz, pattern = DEFAULT_LOCAL_PATTERNS[clazz]!!)
    } else {
      if (!CACHE_PATTERNS.containsKey(pattern)) CACHE_PATTERNS[pattern] = DateTimeFormatter.ofPattern(pattern)
      CACHE_PATTERNS[pattern]!!
    }
  }
}