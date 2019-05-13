package tech.simter.jackson

import cn.gftaxi.webflux.dynamicdto.JavaTimeConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.reactive.config.EnableWebFlux

/**
 * Test [Dto].
 *
 * See [1.11.3. Conversion, formatting](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-config-conversion).
 *
 * @author RJ
 */
@Configuration
@EnableWebFlux
@Import(JavaTimeConfiguration::class, JacksonAutoConfiguration::class, WebFluxAutoConfiguration::class)
class ModuleConfiguration {
  private val logger: Logger = LoggerFactory.getLogger(ModuleConfiguration::class.java)
}