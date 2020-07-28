package tech.simter.jackson.javatime.support;

import com.fasterxml.jackson.databind.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.simter.jackson.javatime.JavaTimeFormat;
import tech.simter.jackson.javatime.JavaTimeModule;

import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;

/**
 * An auto configuration from spring-boot.
 *
 * @author RJ
 */
@Configuration
public class JavaTimeConfiguration {
  private final static Logger logger = LoggerFactory.getLogger(JavaTimeConfiguration.class);

  /**
   * Simter Global {@link JavaTimeFormat}s.
   *
   * @return custom java-time formats
   */
  @Bean("simter.jackson.java-time.global-formats")
  @ConfigurationProperties(prefix = "simter.jackson.java-time.global-formats")
  public Map<Class<? extends TemporalAccessor>, JavaTimeFormat> simterGlobalJavaTimeFormats() {
    return new HashMap<>();
  }

  /**
   * Register by method
   * {@link JacksonAutoConfiguration}.Jackson2ObjectMapperBuilderCustomizerConfiguration.StandardJackson2ObjectMapperBuilderCustomizer.configureModules].
   *
   * @param javaTimeFormats the custom java-time formats
   * @return a custom java-time module
   */
  @Bean
  public Module simterJavaTimeModule(
    @Qualifier("simter.jackson.java-time.global-formats")
      Map<Class<? extends TemporalAccessor>, JavaTimeFormat> javaTimeFormats
  ) {
    logger.debug("simter.jackson.java-time.global-formats={}", javaTimeFormats);
    JavaTimeFormat.JAVA_TIME_FORMATS.putAll(javaTimeFormats);
    logger.debug("JavaTimeFormat.JAVA_TIME_FORMATS={}", JavaTimeFormat.JAVA_TIME_FORMATS);
    return JavaTimeModule.INSTANCE;
  }
}
