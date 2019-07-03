package tech.simter.jackson.javatime

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import net.javacrumbs.jsonunit.assertj.JsonAssertion
import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.junit.jupiter.api.Test
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
  }
}