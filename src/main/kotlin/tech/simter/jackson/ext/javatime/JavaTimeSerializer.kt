package tech.simter.jackson.ext.javatime

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.ContextualSerializer
import tech.simter.jackson.ext.javatime.JavaTimeUtils.getFormatter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.Month
import java.time.Year
import java.time.YearMonth
import java.time.temporal.TemporalAccessor
import kotlin.reflect.KClass

/**
 * @author RJ
 */
class JavaTimeSerializer private constructor() : ContextualSerializer, JsonSerializer<TemporalAccessor>() {
  constructor(handledType: KClass<TemporalAccessor>) : this() {
    this.handledType = handledType
  }

  private var handledType: KClass<TemporalAccessor> = TemporalAccessor::class
  override fun createContextual(provider: SerializerProvider, property: BeanProperty): JsonSerializer<TemporalAccessor> {
    @Suppress("UNCHECKED_CAST")
    val handledType = property.type.rawClass.kotlin as KClass<TemporalAccessor>
    return getSerializer(handledType = handledType)
  }

  override fun handledType(): Class<TemporalAccessor> {
    return handledType.java
  }

  override fun serialize(value: TemporalAccessor?, generator: JsonGenerator, provider: SerializerProvider) {
    logger.debug("handledType={}, handledValue={}", handledType, value)
    if (value == null) generator.writeNull()
    when (handledType) {
      Instant::class -> generator.writeNumber((value as Instant).epochSecond)
      Year::class -> generator.writeNumber((value as Year).value)
      Month::class -> generator.writeNumber((value as Month).value)
      YearMonth::class -> generator.writeNumber((value as YearMonth).run { year * 100 + monthValue })
      else -> generator.writeString(getFormatter(clazz = handledType).format(value))
    }
  }

  companion object {
    private val logger: Logger = LoggerFactory.getLogger(JavaTimeSerializer::class.java)
    val INSTANCE = JavaTimeSerializer()
    private val CACHE_SERIALIZERS = mutableMapOf<KClass<TemporalAccessor>, JavaTimeSerializer>()

    fun getSerializer(handledType: KClass<TemporalAccessor>): JavaTimeSerializer {
      if (!CACHE_SERIALIZERS.containsKey(handledType))
        CACHE_SERIALIZERS[handledType] = JavaTimeSerializer(handledType)
      return CACHE_SERIALIZERS[handledType]!!
    }

    fun addAllSupportedSerializerToModule(module: SimpleModule) {
      module.addSerializer(INSTANCE)
    }
  }
}