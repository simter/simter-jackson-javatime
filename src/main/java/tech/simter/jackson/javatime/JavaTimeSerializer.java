package tech.simter.jackson.javatime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.IOException;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;

import static tech.simter.jackson.javatime.JavaTimeFormat.*;

/**
 * @author RJ
 */
class JavaTimeSerializer extends JsonSerializer<TemporalAccessor> implements ContextualSerializer {
  //private final static Logger logger = LoggerFactory.getLogger(JavaTimeSerializer.class);
  public static JavaTimeSerializer INSTANCE = new JavaTimeSerializer();
  private static Map<String, JavaTimeSerializer> CACHE_SERIALIZERS = new HashMap<>();
  private String pattern;
  private JsonFormat.Shape shape;

  private JavaTimeSerializer() {
  }

  protected JavaTimeSerializer(String pattern, JsonFormat.Shape shape) {
    this();
    this.pattern = pattern;
    this.shape = shape;
  }

  public Class<TemporalAccessor> handledType() {
    return TemporalAccessor.class;
  }

  @SuppressWarnings("unchecked")
  public JsonSerializer<TemporalAccessor> createContextual(SerializerProvider provider, BeanProperty property) {
    if (property == null) return this;

    // get custom data-time format config
    String pattern = null;
    JsonFormat.Shape shape = null;
    JsonFormat jsonFormat = property.getAnnotation(JsonFormat.class);
    if (jsonFormat == null) jsonFormat = property.getContextAnnotation(JsonFormat.class);
    if (jsonFormat != null) {
      pattern = jsonFormat.pattern();
      shape = jsonFormat.shape();
    } else { // also can use spring-DateTimeFormat: style to shape
      if (canUseDateTimeFormatAnnotation()) {
        DateTimeFormat dateTimeFormat = property.getAnnotation(DateTimeFormat.class);
        if (dateTimeFormat == null) dateTimeFormat = property.getContextAnnotation(DateTimeFormat.class);
        if (dateTimeFormat != null) {
          pattern = dateTimeFormat.pattern();
          if ("NUMBER".equals(dateTimeFormat.style())) shape = JsonFormat.Shape.NUMBER;
          else if ("STRING".equals(dateTimeFormat.style()) || "SS".equals(dateTimeFormat.style()))
            shape = JsonFormat.Shape.STRING;
          else
            throw new IllegalArgumentException("style value \"" + dateTimeFormat.style() + "\" could not use on property \"" + property.getName() + "\"'s @DateTimeFormat");
        }
      }
    }

    // get Bean property declare type
    Class<TemporalAccessor> handledType = (Class<TemporalAccessor>) property.getType().getRawClass();

    // create a serializer base on config
    return getSerializer(handledType, pattern, shape);
  }

  // judge whether can use @DateTimeFormat annotation instead of @JsonFormat
  private boolean canUseDateTimeFormatAnnotation() {
    boolean canUseDateTimeFormat;
    try {
      Class<?> c = Class.forName("org.springframework.format.annotation.DateTimeFormat");
      canUseDateTimeFormat = true;
    } catch (ClassNotFoundException e) {
      canUseDateTimeFormat = false;
    }
    return canUseDateTimeFormat;
  }

  private static JavaTimeSerializer getSerializer(Class<TemporalAccessor> handledType, String pattern, JsonFormat.Shape shape) {
    if (pattern == null) { // no custom fallback to global
      JavaTimeFormat javaTimeFormat = JavaTimeFormat.JAVA_TIME_FORMATS.get(handledType);
      if (javaTimeFormat != null) {
        pattern = javaTimeFormat.getPattern();
        if (shape == null) shape = javaTimeFormat.getShape();
      }
    }
    String key = (pattern == null ? "" : ("|" + pattern)) + (shape == null ? "" : ("|" + shape.name()));
    if (!CACHE_SERIALIZERS.containsKey(key)) CACHE_SERIALIZERS.put(key, new JavaTimeSerializer(pattern, shape));
    return CACHE_SERIALIZERS.get(key);
  }

  public void serialize(TemporalAccessor value, JsonGenerator generator, SerializerProvider provider) throws IOException {
    if (value == null) generator.writeNull();
    else {
      //System.out.println("  getCurrentValue=" + generator.getCurrentValue());
      Class<? extends TemporalAccessor> valueType = value.getClass(); // get value real type
      String pattern = this.pattern;
      if (pattern == null) pattern = DEFAULT_LOCAL_PATTERNS.get(valueType);
      if (EPOCH_SECOND_PATTERN.equals(pattern) || EPOCH_MILLISECOND_PATTERN.equals(pattern)) {
        // convert to Instant
        Instant instant;
        if (valueType == Instant.class) {
          instant = (Instant) value;
        } else if (valueType == Year.class) {
          instant = ((Year) value).atDay(1).atStartOfDay().atOffset(LOCAL_OFFSET).toInstant();
        } else if (valueType == YearMonth.class) {
          instant = ((YearMonth) value).atDay(1).atStartOfDay().atOffset(LOCAL_OFFSET).toInstant();
        } else if (valueType == LocalDate.class) {
          instant = ((LocalDate) value).atStartOfDay().atOffset(LOCAL_OFFSET).toInstant();
        } else if (valueType == LocalDateTime.class) {
          instant = ((LocalDateTime) value).atOffset(LOCAL_OFFSET).toInstant();
        } else if (valueType == OffsetDateTime.class) {
          instant = ((OffsetDateTime) value).toInstant();
        } else if (valueType == ZonedDateTime.class) {
          instant = ((ZonedDateTime) value).toInstant();
        } else throw new IllegalArgumentException(pattern + " could not use on " + valueType);

        // write out
        long v;
        if (EPOCH_SECOND_PATTERN.equals(pattern)) v = instant.getEpochSecond();
        else v = instant.toEpochMilli();
        if (isWriteToNumber(valueType, shape)) generator.writeNumber(v);
        else generator.writeString(String.valueOf(v));
      } else {
        // format it to string-value
        String formattedValue = JavaTimeFormat.getFormatter(valueType, pattern).format(value);

        // write out
        if (isWriteToNumber(valueType, shape)) generator.writeNumber(Long.parseLong(formattedValue));
        else generator.writeString(formattedValue);
      }
    }
  }

  private static boolean isWriteToNumber(Class<? extends TemporalAccessor> valueType, JsonFormat.Shape shape) {
    if (shape == null && JAVA_TIME_FORMATS.containsKey(valueType)) // get global config
      shape = JAVA_TIME_FORMATS.get(valueType).getShape();
    return shape == JsonFormat.Shape.NUMBER || shape == JsonFormat.Shape.NUMBER_INT || shape == JsonFormat.Shape.NUMBER_FLOAT;
  }

  static void addAllSupportedSerializerToModule(SimpleModule module) {
    module.addSerializer(INSTANCE);
  }
}