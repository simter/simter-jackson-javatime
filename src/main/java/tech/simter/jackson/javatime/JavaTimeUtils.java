package tech.simter.jackson.javatime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RJ
 */
public final class JavaTimeUtils {
  public final static ZoneOffset LOCAL_OFFSET = OffsetDateTime.now().getOffset();
  public final static String LOCAL_DATE_PATTERN = "yyyy-MM-dd";
  public final static String LOCAL_TIME_PATTERN = "HH:mm";
  public final static String LOCAL_DATE_TIME_PATTERN = LOCAL_DATE_PATTERN + " " + LOCAL_TIME_PATTERN;
  public final static String LOCAL_MONTH_DAY_PATTERN = "MM-dd";
  private final static String DEFAULT_FORMATTER_KEY = "DEFAULT";
  private final static Map<String, DateTimeFormatter> CACHE_PATTERNS = new HashMap<>();
  public final static Map<Class<? extends TemporalAccessor>, String> DEFAULT_LOCAL_PATTERNS = new HashMap<>();

  static {
    CACHE_PATTERNS.put(DEFAULT_FORMATTER_KEY, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    DEFAULT_LOCAL_PATTERNS.put(LocalDateTime.class, LOCAL_DATE_TIME_PATTERN);
    DEFAULT_LOCAL_PATTERNS.put(LocalDate.class, LOCAL_DATE_PATTERN);
    DEFAULT_LOCAL_PATTERNS.put(LocalTime.class, LOCAL_TIME_PATTERN);
    DEFAULT_LOCAL_PATTERNS.put(MonthDay.class, LOCAL_MONTH_DAY_PATTERN);
    DEFAULT_LOCAL_PATTERNS.put(OffsetDateTime.class, LOCAL_DATE_TIME_PATTERN);
    DEFAULT_LOCAL_PATTERNS.put(OffsetTime.class, LOCAL_TIME_PATTERN);
    DEFAULT_LOCAL_PATTERNS.put(ZonedDateTime.class, LOCAL_DATE_TIME_PATTERN);
  }

  public static DateTimeFormatter getFormatter(Class<? extends TemporalAccessor> clazz) {
    return getFormatter(clazz, null);
  }

  public static DateTimeFormatter getFormatter(Class<? extends TemporalAccessor> clazz, String pattern) {
    if (pattern == null || pattern.isEmpty()) {
      return getFormatter(clazz, DEFAULT_LOCAL_PATTERNS.get(clazz));
    } else {
      if (!CACHE_PATTERNS.containsKey(pattern)) CACHE_PATTERNS.put(pattern, DateTimeFormatter.ofPattern(pattern));
      return CACHE_PATTERNS.get(pattern);
    }
  }
}