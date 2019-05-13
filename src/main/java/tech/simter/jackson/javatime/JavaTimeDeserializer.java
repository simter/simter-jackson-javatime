package tech.simter.jackson.javatime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.time.*;
import java.time.temporal.TemporalAccessor;

class JavaTimeDeserializer<T extends TemporalAccessor> extends JsonDeserializer<T> {
  //private final static Logger logger = LoggerFactory.getLogger(JavaTimeDeserializer.class);
  private Class<T> handledType;

  private JavaTimeDeserializer(Class<T> handledType) {
    this.handledType = handledType;
  }

  @SuppressWarnings("unchecked")
  public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    String value = parser.getText();
    //logger.debug("handledType={}, handledValue={}", handledType, value);
    return (T) value2TemporalAccessor(value, handledType);
  }

  static TemporalAccessor value2TemporalAccessor(String value, Class<? extends TemporalAccessor> targetClass) {
    if (targetClass == LocalDateTime.class) return LocalDateTime.parse(value, JavaTimeUtils.getFormatter(targetClass));
    if (targetClass == LocalDate.class) return LocalDate.parse(value, JavaTimeUtils.getFormatter(targetClass));
    if (targetClass == LocalTime.class) return LocalTime.parse(value, JavaTimeUtils.getFormatter(targetClass));
    if (targetClass == OffsetDateTime.class) return OffsetDateTime.of(
      LocalDateTime.parse(value, JavaTimeUtils.getFormatter(targetClass)),
      JavaTimeUtils.LOCAL_OFFSET
    );
    if (targetClass == OffsetTime.class) return OffsetTime.of(
      LocalTime.parse(value, JavaTimeUtils.getFormatter(targetClass)),
      JavaTimeUtils.LOCAL_OFFSET
    );
    if (targetClass == ZonedDateTime.class) return ZonedDateTime.of(
      LocalDateTime.parse(value, JavaTimeUtils.getFormatter(targetClass)),
      ZoneId.systemDefault()
    );
    if (targetClass == Instant.class) return Instant.ofEpochSecond(Long.parseLong(value));
    if (targetClass == YearMonth.class)
      return YearMonth.of(Integer.parseInt(value.substring(0, 4)), Integer.parseInt(value.substring(4)));
    if (targetClass == Year.class) return Year.of(Integer.parseInt(value));
    if (targetClass == Month.class) return Month.of(Integer.parseInt(value));
    if (targetClass == MonthDay.class) return MonthDay.parse(value, JavaTimeUtils.getFormatter(targetClass));
    else throw new UnsupportedOperationException("handledType=$targetClass, handledValue=$value");
  }

  @SuppressWarnings("unchecked")
  public static void addAllSupportedDeserializerToModule(SimpleModule module) {
    module.addDeserializer(LocalDateTime.class, new JavaTimeDeserializer(LocalDateTime.class));
    module.addDeserializer(LocalDate.class, new JavaTimeDeserializer(LocalDate.class));
    module.addDeserializer(LocalTime.class, new JavaTimeDeserializer(LocalTime.class));
    module.addDeserializer(OffsetDateTime.class, new JavaTimeDeserializer(OffsetDateTime.class));
    module.addDeserializer(OffsetTime.class, new JavaTimeDeserializer(OffsetTime.class));
    module.addDeserializer(ZonedDateTime.class, new JavaTimeDeserializer(ZonedDateTime.class));

    module.addDeserializer(Instant.class, new JavaTimeDeserializer(Instant.class));
    module.addDeserializer(YearMonth.class, new JavaTimeDeserializer(YearMonth.class));
    module.addDeserializer(Year.class, new JavaTimeDeserializer(Year.class));
    module.addDeserializer(Month.class, new JavaTimeDeserializer(Month.class));
    module.addDeserializer(MonthDay.class, new JavaTimeDeserializer(MonthDay.class));
  }
}