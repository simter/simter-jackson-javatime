package tech.simter.jackson.javatime

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.simter.jackson.Dto
import tech.simter.jackson.javatime.JavaTimeDeserializer.Companion.addAllSupportedDeserializerToModule
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Test [JavaTimeDeserializer].
 *
 * @author RJ
 */
class JavaTimeDeserializerTest {
  private val logger: Logger = LoggerFactory.getLogger(JavaTimeDeserializerTest::class.java)

  @Test
  fun testDeserializer() {
    // config
    val mapper = ObjectMapper()
    val module = SimpleModule("MyModule", Version(1, 0, 0, null, null, null))
    addAllSupportedDeserializerToModule(module)
    mapper.registerModule(module)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)

    // init data
    val now = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES)
    val dateTime2minutes = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    val json = """
      {
        "localDateTime": "$dateTime2minutes",
        "localDate": "${dateTime2minutes.substring(0, 10)}",
        "localTime": "${dateTime2minutes.substring(11, 16)}",
        "offsetDateTime": "$dateTime2minutes",
        "offsetTime": "${dateTime2minutes.substring(11, 16)}",
        "zonedDateTime": "$dateTime2minutes",
        "instant": ${now.toInstant().epochSecond.toInt()},
        "yearMonth": ${now.year * 100 + now.monthValue},
        "year": ${now.year},
        "month": ${now.monthValue},
        "monthDay": "${dateTime2minutes.substring(5, 10)}"
      }
      """.trimIndent()
    logger.debug("json={}", json)

    // do deserialize
    val dto = mapper.readValue(json, Dto::class.java)
    logger.debug("dto={}", dto)

    // verify deserialize
    assertNull(dto.name)
    assertEquals(now.toLocalDateTime(), dto.localDateTime)
    assertEquals(now.toLocalDate(), dto.localDate)
    assertEquals(now.toLocalTime(), dto.localTime)
    assertEquals(now, dto.offsetDateTime)
    assertEquals(now.toZonedDateTime().withZoneSameLocal(ZoneId.systemDefault()), dto.zonedDateTime)

    assertEquals(now.toInstant(), dto.instant)
    assertEquals(YearMonth.of(now.year, now.monthValue), dto.yearMonth)
    assertEquals(Year.of(now.year), dto.year)
    assertEquals(now.month, dto.month)
    assertEquals(MonthDay.of(now.monthValue, now.dayOfMonth), dto.monthDay)
  }
}