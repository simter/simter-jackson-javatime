package tech.simter.jackson.handler

import cn.gftaxi.webflux.dynamicdto.GetHandler
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.springframework.test.web.reactive.server.WebTestClient.bindToRouterFunction
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RouterFunctions.route
import tech.simter.jackson.Example
import tech.simter.jackson.ModuleConfiguration
import java.time.format.DateTimeFormatter

/**
 * Test [GetHandler].
 *
 * See [1.11.3. Conversion, formatting](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-config-conversion).
 *
 * @author RJ
 */
@Disabled
@SpringJUnitConfig(GetHandler::class, ModuleConfiguration::class)
class GetHandlerTest @Autowired constructor(
  private val getHandler: GetHandler
) {
  private val logger: Logger = LoggerFactory.getLogger(GetHandlerTest::class.java)
  private val path = "/"

  @Test
  fun get() {
    val dateTime = Example.instance2minutes.offsetDateTime!!
    val dateTime2minutes = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))!!

    val client = bindToRouterFunction(route(GET(path), getHandler)).build()
    client.get().uri(path)
      .exchange()
      .expectStatus().isOk
      .expectHeader().contentType(APPLICATION_JSON_UTF8)
      .expectBody()
      .consumeWith { logger.debug(String(it.responseBody!!)) }
      .jsonPath("$.id").doesNotExist()
      .jsonPath("$.name").doesNotExist()
      .jsonPath("$.localDateTime").isEqualTo(dateTime2minutes)
      .jsonPath("$.localDateTime").isEqualTo(dateTime2minutes)
      .jsonPath("$.localDate").isEqualTo(dateTime2minutes.substring(0, 10))
      .jsonPath("$.localTime").isEqualTo(dateTime2minutes.substring(11, 16))
      .jsonPath("$.offsetDateTime").isEqualTo(dateTime2minutes)
      .jsonPath("$.offsetTime").isEqualTo(dateTime2minutes.substring(11, 16))
      .jsonPath("$.zonedDateTime").isEqualTo(dateTime2minutes)
      .jsonPath("$.instant").isEqualTo(dateTime.toInstant().epochSecond.toInt())
      .jsonPath("$.yearMonth").isEqualTo(dateTime.year * 100 + dateTime.monthValue)
      .jsonPath("$.year").isEqualTo(dateTime.year)
      .jsonPath("$.month").isEqualTo(dateTime.monthValue)
      .jsonPath("$.monthDay").isEqualTo(dateTime2minutes.substring(5, 10))
  }

//  @Test
//  fun patch() {
//    val client = bindToRouterFunction(route(PATCH(path), patchHandler)).build()
//    val json = """
//      {
//        "name": "${DEFAULT_DTO1.name}",
//        "decimal": ${DEFAULT_DTO1.decimal},
//        "localDate": "$localDate",
//        "offsetDateTime": "$offsetDateTime"
//      }""".trimIndent()
//
//    client.patch().uri(path)
//      .contentType(APPLICATION_JSON_UTF8)
//      .syncBody(json)
//      .exchange()
//      .expectStatus().isOk
//      .expectBody()
//      .jsonPath("$.notSet").doesNotExist()
//      .jsonPath("$.name").isEqualTo(DEFAULT_DTO1.name!!)
//      .jsonPath("$.decimal").isEqualTo(DEFAULT_DTO1.decimal!!)
//      // By Example/@set:DateTimeFormat
//      .jsonPath("$.localDate").isEqualTo(localDate)
//      .jsonPath("$.offsetDateTime").isEqualTo(offsetDateTime)
//  }
}