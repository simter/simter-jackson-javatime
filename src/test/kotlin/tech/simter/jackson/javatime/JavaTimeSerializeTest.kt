package tech.simter.jackson.javatime

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.javacrumbs.jsonunit.assertj.JsonAssertion
import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.junit.jupiter.api.Test
import org.springframework.format.annotation.DateTimeFormat
import java.time.*
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
  fun testDefaultGlobalConfig() {
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

  @Test
  fun testCustomFormatByAnnotation() {
    data class Bean(
      val localDateTime0: LocalDateTime?,
      val localDateTime1: LocalDateTime,
      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")     // jackson annotation
      val localDateTime2: LocalDateTime?,
      @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // spring annotation
      val localDateTime3: LocalDateTime
    )
    // config
    val mapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule.INSTANCE);

    // do serialize
    val dt = LocalDateTime.of(2020, 1, 10, 20, 30, 40)
    val json = mapper.writeValueAsString(Bean(
      localDateTime0 = null,
      localDateTime1 = dt,
      localDateTime2 = dt,
      localDateTime3 = dt
    ))

    // verify serialize
    assertThatJson(json).and(
      JsonAssertion { it.node("localDateTime0").isNull() },
      JsonAssertion { it.node("localDateTime1").isEqualTo("2020-01-10 20:30") },
      JsonAssertion { it.node("localDateTime2").isEqualTo("2020-01-10 20:30:40") },
      JsonAssertion { it.node("localDateTime3").isEqualTo("2020-01-10T20:30:40") }
    )
  }

  @Test
  fun testFormatToNumber() {
    data class Bean(
      val year0: Year?,
      val year1: Year, // global default number
      @JsonFormat(pattern = "yyyy") // default string
      val year2: Year,
      @JsonFormat(pattern = "yyyy", shape = JsonFormat.Shape.NUMBER) // to number
      val year3: Year,
      @JsonFormat(pattern = "yyyyMM", shape = JsonFormat.Shape.NUMBER)
      val ym: YearMonth,
      @JsonFormat(pattern = "MM", shape = JsonFormat.Shape.NUMBER)
      val m: Month,
      @JsonFormat(pattern = "MMdd", shape = JsonFormat.Shape.NUMBER)
      val md: MonthDay,
      @JsonFormat(pattern = "EpochSecond", shape = JsonFormat.Shape.NUMBER)
      val instant1: Instant,
      @JsonFormat(pattern = "EpochMillisecond", shape = JsonFormat.Shape.NUMBER)
      val instant2: Instant,
      @JsonFormat(pattern = "EpochSecond", shape = JsonFormat.Shape.NUMBER)
      val localDate: LocalDate,
      @JsonFormat(pattern = "EpochSecond", shape = JsonFormat.Shape.NUMBER)
      val localDateTime: LocalDateTime,
      @JsonFormat(pattern = "EpochSecond", shape = JsonFormat.Shape.NUMBER)
      val offsetDateTime: OffsetDateTime,
      @JsonFormat(pattern = "EpochSecond", shape = JsonFormat.Shape.NUMBER)
      val zonedDateTime: ZonedDateTime
    )
    // config
    val mapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule.INSTANCE);

    // do serialize
    val now = OffsetDateTime.now()
    val y = Year.of(2020)
    val json = mapper.writeValueAsString(Bean(
      year0 = null,
      year1 = y,
      year2 = y,
      year3 = y,
      ym = YearMonth.of(2020, 1),
      m = Month.of(1),
      md = MonthDay.of(1, 10),
      instant1 = now.toInstant(),
      instant2 = now.toInstant(),
      localDate = now.toLocalDate(),
      localDateTime = now.toLocalDateTime(),
      offsetDateTime = now,
      zonedDateTime = now.toZonedDateTime()
    ))

    // verify serialize
    assertThatJson(json).and(
      JsonAssertion { it.node("year0").isNull() },
      JsonAssertion { it.node("year1").isIntegralNumber.isEqualTo(2020) },
      JsonAssertion { it.node("year2").isString.isEqualTo("2020") },
      JsonAssertion { it.node("year3").isIntegralNumber.isEqualTo(2020) },
      JsonAssertion { it.node("ym").isIntegralNumber.isEqualTo(202001) },
      JsonAssertion { it.node("m").isIntegralNumber.isEqualTo(1) },
      JsonAssertion { it.node("md").isIntegralNumber.isEqualTo(110) },
      JsonAssertion { it.node("instant1").isIntegralNumber.isEqualTo(now.toInstant().epochSecond) },
      JsonAssertion { it.node("instant2").isIntegralNumber.isEqualTo(now.toInstant().toEpochMilli()) },
      JsonAssertion { it.node("localDate").isIntegralNumber.isEqualTo(now.truncatedTo(ChronoUnit.DAYS).toInstant().epochSecond) },
      JsonAssertion { it.node("localDateTime").isIntegralNumber.isEqualTo(now.truncatedTo(ChronoUnit.SECONDS).toInstant().epochSecond) },
      JsonAssertion { it.node("offsetDateTime").isIntegralNumber.isEqualTo(now.toInstant().epochSecond) },
      JsonAssertion { it.node("zonedDateTime").isIntegralNumber.isEqualTo(now.toInstant().epochSecond) }
    )
  }

  @Test
  fun testSingleValue() {
    // config
    val mapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule.INSTANCE);
    val dt = OffsetDateTime.of(2020, 1, 10, 20, 30, 40, 0, ZoneOffset.UTC)

    // serialize then verify
    assertThatJson(mapper.writeValueAsString(dt)).isString.isEqualTo("2020-01-10 20:30");
    assertThatJson(mapper.writeValueAsString(dt.toZonedDateTime())).isString.isEqualTo("2020-01-10 20:30");
    assertThatJson(mapper.writeValueAsString(dt.toOffsetTime())).isString.isEqualTo("20:30");
    assertThatJson(mapper.writeValueAsString(dt.toLocalDateTime())).isString.isEqualTo("2020-01-10 20:30");
    assertThatJson(mapper.writeValueAsString(dt.toLocalDate())).isString.isEqualTo("2020-01-10");
    assertThatJson(mapper.writeValueAsString(dt.toLocalTime())).isString.isEqualTo("20:30");
    assertThatJson(mapper.writeValueAsString(YearMonth.of(dt.year, dt.month))).isIntegralNumber.isEqualTo(202001);
    assertThatJson(mapper.writeValueAsString(dt.month)).isIntegralNumber.isEqualTo(1);
    assertThatJson(mapper.writeValueAsString(MonthDay.of(dt.month, dt.dayOfMonth))).isString.isEqualTo("01-10");
    assertThatJson(mapper.writeValueAsString(Year.of(dt.year))).isIntegralNumber.isEqualTo(2020);
    assertThatJson(mapper.writeValueAsString(dt.toInstant())).isIntegralNumber.isEqualTo(dt.toEpochSecond());
  }

  @Test
  fun testInMap() {
    // config
    val mapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule.INSTANCE);

    // do serialize
    val dt = OffsetDateTime.of(2020, 1, 10, 20, 30, 40, 0, ZoneOffset.UTC)
    val json = mapper.writeValueAsString(mapOf(
      "offsetDateTime" to dt,
      "zonedDateTime" to dt.toZonedDateTime(),
      "offsetTime" to dt.toOffsetTime(),
      "localDateTime" to dt.toLocalDateTime(),
      "localDate" to dt.toLocalDate(),
      "localTime" to dt.toLocalTime(),
      "yearMonth" to YearMonth.of(dt.year, dt.month),
      "month" to dt.month,
      "monthDay" to MonthDay.of(dt.month, dt.dayOfMonth),
      "year" to Year.of(dt.year),
      "instant" to dt.toInstant()
    ))

    // verify serialize
    assertThatJson(json).and(
      JsonAssertion { it.node("zonedDateTime").isString.isEqualTo("2020-01-10 20:30") },
      JsonAssertion { it.node("offsetDateTime").isString.isEqualTo("2020-01-10 20:30") },
      JsonAssertion { it.node("offsetTime").isString.isEqualTo("20:30") },
      JsonAssertion { it.node("localDateTime").isString.isEqualTo("2020-01-10 20:30") },
      JsonAssertion { it.node("localDate").isString.isEqualTo("2020-01-10") },
      JsonAssertion { it.node("localTime").isString.isEqualTo("20:30") },
      JsonAssertion { it.node("yearMonth").isIntegralNumber.isEqualTo(202001) },
      JsonAssertion { it.node("month").isIntegralNumber.isEqualTo(1) },
      JsonAssertion { it.node("monthDay").isString.isEqualTo("01-10") },
      JsonAssertion { it.node("year").isIntegralNumber.isEqualTo(2020) },
      JsonAssertion { it.node("instant").isIntegralNumber.isEqualTo(dt.toEpochSecond()) }
    )
  }
}