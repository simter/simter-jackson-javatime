package tech.simter.jackson.javatime.support

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import java.time.LocalDateTime

/**
 * @author RJ
 */
@SpringBootTest
@ComponentScan
class JavaTimeConfigurationTest @Autowired constructor(private val objectMapper: ObjectMapper) {
  @Test
  fun test() {
    println(objectMapper.writeValueAsString(Bean1(
      name = "Test1",
      localDateTime1 = LocalDateTime.now(),
      localDateTime2 = LocalDateTime.now()
    )))
    println(objectMapper.writeValueAsString(Bean2(
      name = "Test2",
      localDateTime1 = LocalDateTime.now(),
      localDateTime2 = LocalDateTime.now()
    )))
  }

  data class Bean1(
    val name: String,
    val localDateTime1: LocalDateTime,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val localDateTime2: LocalDateTime
  )

  data class Bean2(
    val name: String,
    val localDateTime1: LocalDateTime,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val localDateTime2: LocalDateTime
  )
}