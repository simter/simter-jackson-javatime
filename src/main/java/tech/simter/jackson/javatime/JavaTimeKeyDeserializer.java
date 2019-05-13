package tech.simter.jackson.javatime;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.time.*;
import java.time.temporal.TemporalAccessor;

import static tech.simter.jackson.javatime.JavaTimeDeserializer.value2TemporalAccessor;

/**
 * @author RJ
 */
public class JavaTimeKeyDeserializer<T extends TemporalAccessor> extends KeyDeserializer {
  //private final static Logger logger = LoggerFactory.getLogger(JavaTimeKeyDeserializer.class);
  private Class<T> handledType;

  private JavaTimeKeyDeserializer(Class<T> handledType) {
    this.handledType = handledType;
  }

  @SuppressWarnings("unchecked")
  public T deserializeKey(String key, DeserializationContext context) {
    //logger.debug("handledType={}, handledValue={}", handledType, key);
    return (T) value2TemporalAccessor(key, handledType);
  }

  @SuppressWarnings("unchecked")
  public static void addAllSupportedKeyDeserializerToModule(SimpleModule module) {
    module.addKeyDeserializer(LocalDateTime.class, new JavaTimeKeyDeserializer(LocalDateTime.class));
    module.addKeyDeserializer(LocalDate.class, new JavaTimeKeyDeserializer(LocalDate.class));
    module.addKeyDeserializer(LocalTime.class, new JavaTimeKeyDeserializer(LocalTime.class));
    module.addKeyDeserializer(OffsetDateTime.class, new JavaTimeKeyDeserializer(OffsetDateTime.class));
    module.addKeyDeserializer(OffsetTime.class, new JavaTimeKeyDeserializer(OffsetTime.class));
    module.addKeyDeserializer(ZonedDateTime.class, new JavaTimeKeyDeserializer(ZonedDateTime.class));

    module.addKeyDeserializer(Instant.class, new JavaTimeKeyDeserializer(Instant.class));
    module.addKeyDeserializer(YearMonth.class, new JavaTimeKeyDeserializer(YearMonth.class));
    module.addKeyDeserializer(Year.class, new JavaTimeKeyDeserializer(Year.class));
    module.addKeyDeserializer(Month.class, new JavaTimeKeyDeserializer(Month.class));
    module.addKeyDeserializer(MonthDay.class, new JavaTimeKeyDeserializer(MonthDay.class));
  }
}