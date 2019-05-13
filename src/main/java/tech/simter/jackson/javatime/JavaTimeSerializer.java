package tech.simter.jackson.javatime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.time.Instant;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RJ
 */
class JavaTimeSerializer extends JsonSerializer<TemporalAccessor> implements ContextualSerializer {
  //private final static Logger logger = LoggerFactory.getLogger(JavaTimeSerializer.class);
  public static JavaTimeSerializer INSTANCE = new JavaTimeSerializer();
  private static Map<Class<TemporalAccessor>, JavaTimeSerializer> CACHE_SERIALIZERS = new HashMap<>();

  private JavaTimeSerializer() {
  }

  private JavaTimeSerializer(Class<TemporalAccessor> handledType) {
    this();
    this.handledType = handledType;
  }

  private Class<TemporalAccessor> handledType = TemporalAccessor.class;

  @SuppressWarnings("unchecked")
  public JsonSerializer<TemporalAccessor> createContextual(SerializerProvider provider, BeanProperty property) {
    Class<TemporalAccessor> handledType = null;
    if (property != null) handledType = (Class<TemporalAccessor>) property.getType().getRawClass();
    if (handledType == null) handledType = this.handledType;
    return getSerializer(handledType);
  }

  public Class<TemporalAccessor> handledType() {
    return handledType;
  }

  public void serialize(TemporalAccessor value, JsonGenerator generator, SerializerProvider provider) throws IOException {
    //logger.debug("handledType={}, handledValue={}", handledType, value);
    if (value == null) generator.writeNull();
    else {
      Class<? extends TemporalAccessor> valueType = value.getClass(); // get value real type
      if (valueType == Instant.class) {
        generator.writeNumber(((Instant) value).getEpochSecond());
      } else if (valueType == Year.class) {
        generator.writeNumber(((Year) value).getValue());
      } else if (valueType == Month.class) {
        generator.writeNumber(((Month) value).getValue());
      } else if (valueType == YearMonth.class) {
        YearMonth ym = (YearMonth) value;
        generator.writeNumber(ym.getYear() * 100 + ym.getMonthValue());
      } else {
        generator.writeString(JavaTimeUtils.getFormatter(valueType, null).format(value));
      }
    }
  }

  private static JavaTimeSerializer getSerializer(Class<TemporalAccessor> handledType) {
    if (handledType == null) return null;
    if (!CACHE_SERIALIZERS.containsKey(handledType))
      CACHE_SERIALIZERS.put(handledType, new JavaTimeSerializer(handledType));
    return CACHE_SERIALIZERS.get(handledType);
  }

  static void addAllSupportedSerializerToModule(SimpleModule module) {
    module.addSerializer(INSTANCE);
  }
}