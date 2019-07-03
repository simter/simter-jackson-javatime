package tech.simter.jackson.javatime

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import net.javacrumbs.jsonunit.assertj.JsonAssertion
import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Test [JavaTimeModule].
 *
 * @author RJ
 */
class JavaTimeModuleTest {
  private val now = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES)!!
  private val nowStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))!!

  @Test
  fun test() {
    // config
    val mapper = ObjectMapper()
    mapper.registerModule(JavaTimeModule())
    mapper.setSerializationInclusion(NON_EMPTY) // not serialize null and empty value
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)

    // init data
    val expected = Example(
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

    // do serialize
    val json = mapper.writeValueAsString(expected)

    // verify serialize
    val dateTime2minutes = nowStr.substring(0, 16)
    assertThatJson(json).and(
      JsonAssertion { it.node("name").isAbsent() },
      JsonAssertion { it.node("localDateTime").isEqualTo(dateTime2minutes) },
      JsonAssertion { it.node("localDate").isEqualTo(nowStr.substring(0, 10)) },
      JsonAssertion { it.node("localTime").isEqualTo(nowStr.substring(11, 16)) },
      JsonAssertion { it.node("offsetDateTime").isEqualTo(dateTime2minutes) },
      JsonAssertion { it.node("offsetTime").isEqualTo(nowStr.substring(11, 16)) },
      JsonAssertion { it.node("zonedDateTime").isEqualTo(dateTime2minutes) },
      JsonAssertion { it.node("instant").isEqualTo(now.toInstant().epochSecond.toInt()) },
      JsonAssertion { it.node("yearMonth").isEqualTo(now.year * 100 + now.monthValue) },
      JsonAssertion { it.node("year").isEqualTo(now.year) },
      JsonAssertion { it.node("month").isEqualTo(now.monthValue) },
      JsonAssertion { it.node("monthDay").isEqualTo(nowStr.substring(5, 10)) }
    )

    // do deserialize
    val actual = mapper.readValue(json, Example::class.java)

    // verify deserialize
    assertNull(actual.name)
    assertEquals(now.toLocalDateTime(), actual.localDateTime)
    assertEquals(now.toLocalDate(), actual.localDate)
    assertEquals(now.toLocalTime(), actual.localTime)
    assertEquals(now, actual.offsetDateTime)
    assertEquals(now.toZonedDateTime().withZoneSameLocal(ZoneId.systemDefault()), actual.zonedDateTime)

    assertEquals(now.toInstant(), actual.instant)
    assertEquals(YearMonth.of(now.year, now.monthValue), actual.yearMonth)
    assertEquals(Year.of(now.year), actual.year)
    assertEquals(now.month, actual.month)
    assertEquals(MonthDay.of(now.monthValue, now.dayOfMonth), actual.monthDay)
  }
}