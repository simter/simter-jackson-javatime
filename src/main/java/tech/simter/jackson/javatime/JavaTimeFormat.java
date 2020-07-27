package tech.simter.jackson.javatime;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.NUMBER;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

public class JavaTimeFormat {
  public static final ZoneOffset LOCAL_OFFSET = OffsetDateTime.now().getOffset();
  public static final String EPOCH_SECOND_PATTERN = "EpochSecond";
  public static final String EPOCH_MILLISECOND_PATTERN = "EpochMillisecond";
  public static final String LOCAL_DATE_PATTERN = "yyyy-MM-dd";
  public static final String LOCAL_YEAR_PATTERN = "yyyy";
  public static final String LOCAL_YEAR_MONTH_PATTERN = "yyyyMM";
  public static final String LOCAL_MONTH_DAY_PATTERN = "MM-dd";
  public static final String LOCAL_MONTH_PATTERN = "M";
  public static final String LOCAL_TIME_PATTERN = "HH:mm";
  public static final String LOCAL_DATE_TIME_PATTERN = LOCAL_DATE_PATTERN + " " + LOCAL_TIME_PATTERN;

  /**
   * Global java-time format config
   */
  public static final Map<Class<? extends TemporalAccessor>, JavaTimeFormat> JAVA_TIME_FORMATS = new HashMap<>();

  private final static Map<String, DateTimeFormatter> CACHE_PATTERNS = new HashMap<>();
  public final static Map<Class<? extends TemporalAccessor>, String> DEFAULT_LOCAL_PATTERNS = new HashMap<>();

  static DateTimeFormatter getFormatter(Class<? extends TemporalAccessor> clazz) {
    return getFormatter(clazz, null);
  }

  static DateTimeFormatter getFormatter(Class<? extends TemporalAccessor> clazz, String pattern) {
    if (pattern == null) pattern = DEFAULT_LOCAL_PATTERNS.get(clazz);
    if (pattern == null) {
      throw new IllegalArgumentException("Parameter 'pattern' could not be null");
    } else {
      if (!CACHE_PATTERNS.containsKey(pattern)) CACHE_PATTERNS.put(pattern, DateTimeFormatter.ofPattern(pattern));
      return CACHE_PATTERNS.get(pattern);
    }
  }

  static {
    // initial global default format
    JAVA_TIME_FORMATS.put(Instant.class, new JavaTimeFormat(EPOCH_SECOND_PATTERN, NUMBER));
    JAVA_TIME_FORMATS.put(ZonedDateTime.class, new JavaTimeFormat(LOCAL_DATE_TIME_PATTERN, STRING));
    JAVA_TIME_FORMATS.put(OffsetDateTime.class, new JavaTimeFormat(LOCAL_DATE_TIME_PATTERN, STRING));
    JAVA_TIME_FORMATS.put(OffsetTime.class, new JavaTimeFormat(LOCAL_TIME_PATTERN, STRING));
    JAVA_TIME_FORMATS.put(LocalDateTime.class, new JavaTimeFormat(LOCAL_DATE_TIME_PATTERN, STRING));
    JAVA_TIME_FORMATS.put(LocalDate.class, new JavaTimeFormat(LOCAL_DATE_PATTERN, STRING));
    JAVA_TIME_FORMATS.put(LocalTime.class, new JavaTimeFormat(LOCAL_TIME_PATTERN, STRING));
    JAVA_TIME_FORMATS.put(Year.class, new JavaTimeFormat(LOCAL_YEAR_PATTERN, NUMBER));
    JAVA_TIME_FORMATS.put(YearMonth.class, new JavaTimeFormat(LOCAL_YEAR_MONTH_PATTERN, NUMBER));
    JAVA_TIME_FORMATS.put(Month.class, new JavaTimeFormat(LOCAL_MONTH_PATTERN, NUMBER));
    JAVA_TIME_FORMATS.put(MonthDay.class, new JavaTimeFormat(LOCAL_MONTH_DAY_PATTERN, STRING));

    JAVA_TIME_FORMATS.forEach((key, value) -> {
      DEFAULT_LOCAL_PATTERNS.put(key, value.pattern);
    });
  }

  public JavaTimeFormat(String pattern, JsonFormat.Shape shape) {
    this.pattern = pattern;
    this.shape = shape;
  }

  /**
   * Support Set to {@link #EPOCH_SECOND_PATTERN}, {@link #EPOCH_MILLISECOND_PATTERN} or something like "yyyy-MM-dd HH:mm:ss"
   */
  private String pattern;

  /**
   * The json value type.
   * <p>
   * Only support {@link JsonFormat.Shape#STRING} and {@link JsonFormat.Shape#NUMBER}
   */
  private JsonFormat.Shape shape = JsonFormat.Shape.STRING;

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public JsonFormat.Shape getShape() {
    return shape;
  }

  public void setShape(JsonFormat.Shape shape) {
    this.shape = shape;
  }

  @Override
  public String toString() {
    return "{pattern: \"" + this.getPattern() + "\", shape: \"" + this.getShape() + "\"}";
  }
}
