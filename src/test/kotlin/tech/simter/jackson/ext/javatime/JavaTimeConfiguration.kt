package cn.gftaxi.webflux.dynamicdto

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import tech.simter.jackson.ext.javatime.JavaTimeModule

/**
 * @author RJ
 *
 * See spring-boot document [76.3 Customize the Jackson ObjectMapper](https://docs.spring.io/spring-boot/docs/2.0.4.RELEASE/reference/htmlsingle/#howto-customize-the-jackson-objectmapper).
 */
@Configuration
class JavaTimeConfiguration {
  /**
   * [JacksonAutoConfiguration.JacksonObjectMapperConfiguration.jacksonObjectMapper]
   */
  @Primary
  @Bean
  fun jacksonObjectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper {
//    val objectMapper = builder.createXmlMapper(false).build<ObjectMapper>()
//    objectMapper.registerModule(JavaTimeModule())
//    return objectMapper

    val mapper = ObjectMapper()
    mapper.registerModule(JavaTimeModule())
    mapper.setSerializationInclusion(Include.NON_EMPTY) // not serialize null and empty value
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
    return mapper
  }

  /**
   * Register by method
   * [JacksonAutoConfiguration.Jackson2ObjectMapperBuilderCustomizerConfiguration.StandardJackson2ObjectMapperBuilderCustomizer.configureModules]
   */
  @Bean
  fun customJavaTimeModule(): Module {
    return JavaTimeModule()
  }

  /**
   * Register by method [JacksonAutoConfiguration.JacksonObjectMapperBuilderConfiguration.jacksonObjectMapperBuilder]
   */
  @Bean
  fun customJackson(): Jackson2ObjectMapperBuilderCustomizer {
    // WebFluxConfigurer
    return Jackson2ObjectMapperBuilderCustomizer {
      // not serialize null and empty value
      it.serializationInclusion(Include.NON_EMPTY)

      it.featuresToDisable(
        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE,
        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
      )
      it.featuresToEnable(
        DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT
      )
    }
  }

  // org.springframework.boot.jackson.JsonComponentModule
  // jackson-module-parameter-names
}