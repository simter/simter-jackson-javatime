package tech.simter.jackson.javatime

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.jayway.jsonpath.matchers.JsonPathMatchers.*
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.MonthDay
import java.time.OffsetDateTime
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Test [JavaTimeSerializer].
 *
 * See [POJOs to JSON and back](https://github.com/FasterXML/jackson-databind/#1-minute-tutorial-pojos-to-json-and-back)
 *
 * @author RJ
 */
class JavaTimeSerializeTest {
  private val logger: Logger = LoggerFactory.getLogger(JavaTimeSerializeTest::class.java)
  private val now = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS)!!
  private val nowStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))!!

  @Test
  fun test() {
    // config
    val mapper = ObjectMapper()
    val testModule = SimpleModule("MyModule", Version(1, 0, 0, null, null, null))
    testModule.addSerializer(JavaTimeSerializer.INSTANCE)
    mapper.registerModule(testModule)
    mapper.setSerializationInclusion(NON_EMPTY) // not serialize null and empty value

    // init data
    val dto = Example(
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
    val json = mapper.writeValueAsString(dto)
    logger.debug("json={}", json)

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
  }
}