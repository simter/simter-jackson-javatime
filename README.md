# simter-jackson-javatime

A brand new [Jackson Java8+ Date & Time] serialization and deserialization module with global localize config.

[Jackson standard JavaTimeModule] has a standard [ISO-8601] format default, and it can not be config to another 
default format globally. That's too bad for real projects. This module do these things good. But it's not a extension 
of [Jackson standard JavaTimeModule]. It's a brand new module for setting global data-time format.

It is config to localize date-time format like `yyyy-MM-dd HH:mm` now, accurate to minute and without zone and 
offset info (global use local zone and offset default). That's more useful in my real projects. it can be 
config to another default format by customization in by [JavaTimeFormat.JAVA_TIME_FORMATS].

For spring-boot project, use property `simter.jackson.java-time.global-formats` to config a custom default format, such as:

```yml
simter.jackson.java-time.global-formats:
  "[java.time.LocalDateTime]":
    pattern: "yyyy-MM-dd HH:mm:ss"
    shape: STRING
```

This is auto config by register [JavaTimeConfiguration] to spring.

## 1. Installation

```xml
<dependency>
  <groupId>tech.simter</groupId>
  <artifactId>simter-jackson-javatime</artifactId>
  <version>${VERSION}</version>
</dependency>
```

Exclude `jackson-datatype-jsr310` for spring-boot:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-json</artifactId>
  <exclusions>
    <exclusion>
      <groupId>com.fasterxml.jackson.datatype</groupId>
      <artifactId>jackson-datatype-jsr310</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```

## 2. Global default format

| DateTime Class | Value-Format     | Serialize To |
|----------------|------------------|--------------|
| OffsetDateTime | yyyy-MM-dd HH:mm | String       |
| OffsetTime     | yyyy-MM-dd HH:mm | String       |
| ZonedDateTime  | yyyy-MM-dd HH:mm | String       |
| LocalDateTime  | yyyy-MM-dd HH:mm | String       |
| LocalDate      | yyyy-MM-dd       | String       |
| LocalTime      | HH:mm            | String       |
| Instant        | {EpochSecond}    | Number       |
| YearMonth      | yyyyMM           | Number       |
| Year           | yyyy             | Number       |
| Month          | MM               | Number       |
| MonthDay       | MM-dd            | String       |

## 3. Usage

### 3.1. Base usage

```
val mapper = ObjectMapper()
mapper.registerModule(tech.simter.jackson.ext.javatime.JavaTimeModule())

// Recommand to disabled and enabled some features:
mapper.setSerializationInclusion(NON_EMPTY)
mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
```

### 3.2. WebFlux usage

See [simter-reactive-web/.../WebFluxConfiguration.kt].


[JavaTimeFormat.JAVA_TIME_FORMATS]: https://github.com/simter/simter-jackson-javatime/blob/master/src/main/java/tech/simter/jackson/javatime/JavaTimeFormat.java#L29
[JavaTimeConfiguration]: https://github.com/simter/simter-jackson-javatime/blob/master/src/main/java/tech/simter/jackson/javatime/support/JavaTimeConfiguration.java
[Jackson Java8+ Date & Time]: https://github.com/FasterXML/jackson-modules-java8/tree/master/datetime
[Jackson standard JavaTimeModule]: https://github.com/FasterXML/jackson-modules-java8/blob/master/datetime/src/main/java/com/fasterxml/jackson/datatype/jsr310/JavaTimeModule.java
[ISO-8601]: http://en.wikipedia.org/wiki/ISO_8601
[simter-reactive-web/.../WebFluxConfiguration.kt]: https://github.com/simter/simter-reactive-web/blob/master/src/main/kotlin/tech/simter/reactive/web/webflux/WebFluxConfiguration.kt