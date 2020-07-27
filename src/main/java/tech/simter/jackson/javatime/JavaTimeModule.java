package tech.simter.jackson.javatime;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author RJ
 */
public class JavaTimeModule extends SimpleModule {
  public static final JavaTimeModule INSTANCE = new JavaTimeModule();

  public JavaTimeModule() {
  }

  static {
    JavaTimeSerializer.addAllSupportedSerializerToModule(INSTANCE);
    JavaTimeDeserializer.addAllSupportedDeserializerToModule(INSTANCE);
    JavaTimeKeyDeserializer.addAllSupportedKeyDeserializerToModule(INSTANCE);
  }
}