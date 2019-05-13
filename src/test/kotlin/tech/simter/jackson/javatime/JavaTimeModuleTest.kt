package tech.simter.jackson.javatime

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.jayway.jsonpath.matchers.JsonPathMatchers.*
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
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
    assertThat(json, isJson(allOf(
      withoutJsonPath("$.name"),
      withJsonPath("$.localDateTime", equalTo(dateTime2minutes)),
      withJsonPath("$.localDate", equalTo(nowStr.substring(0, 10))),
      withJsonPath("$.localTime", equalTo(nowStr.substring(11, 16))),
      withJsonPath("$.offsetDateTime", equalTo(dateTime2minutes)),
      withJsonPath("$.offsetTime", equalTo(nowStr.substring(11, 16))),
      withJsonPath("$.zonedDateTime", equalTo(dateTime2minutes)),
      withJsonPath("$.instant", equalTo(now.toInstant().epochSecond.toInt())),
      withJsonPath("$.yearMonth", equalTo(now.year * 100 + now.monthValue)),
      withJsonPath("$.year", equalTo(now.year)),
      withJsonPath("$.month", equalTo(now.monthValue)),
      withJsonPath("$.monthDay", equalTo(nowStr.substring(5, 10)))
    )))

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