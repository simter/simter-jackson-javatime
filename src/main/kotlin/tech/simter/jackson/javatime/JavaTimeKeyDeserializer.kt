package tech.simter.jackson.javatime

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import tech.simter.jackson.javatime.JavaTimeDeserializer.Companion.value2TemporalAccessor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.*
import java.time.temporal.TemporalAccessor
import kotlin.reflect.KClass

/**
 * @author RJ
 */
class JavaTimeKeyDeserializer<out T : TemporalAccessor>(private val handledType: KClass<T>) : KeyDeserializer() {
  @Suppress("UNCHECKED_CAST")
  override fun deserializeKey(key: String, context: DeserializationContext): T {
    logger.debug("handledType={}, handledValue={}", handledType, key)
    return value2TemporalAccessor(key, handledType as KClass<TemporalAccessor>) as T
  }

  companion object {
    private val logger: Logger = LoggerFactory.getLogger(JavaTimeKeyDeserializer::class.java)

    fun addAllSupportedKeyDeserializerToModule(module: SimpleModule) {
      module.addKeyDeserializer(LocalDateTime::class.java, JavaTimeKeyDeserializer(LocalDateTime::class))
      module.addKeyDeserializer(LocalDate::class.java, JavaTimeKeyDeserializer(LocalDate::class))
      module.addKeyDeserializer(LocalTime::class.java, JavaTimeKeyDeserializer(LocalTime::class))
      module.addKeyDeserializer(OffsetDateTime::class.java, JavaTimeKeyDeserializer(OffsetDateTime::class))
      module.addKeyDeserializer(OffsetTime::class.java, JavaTimeKeyDeserializer(OffsetTime::class))
      module.addKeyDeserializer(ZonedDateTime::class.java, JavaTimeKeyDeserializer(ZonedDateTime::class))

      module.addKeyDeserializer(Instant::class.java, JavaTimeKeyDeserializer(Instant::class))
      module.addKeyDeserializer(YearMonth::class.java, JavaTimeKeyDeserializer(YearMonth::class))
      module.addKeyDeserializer(Year::class.java, JavaTimeKeyDeserializer(Year::class))
      module.addKeyDeserializer(Month::class.java, JavaTimeKeyDeserializer(Month::class))
      module.addKeyDeserializer(MonthDay::class.java, JavaTimeKeyDeserializer(MonthDay::class))
    }
  }
}