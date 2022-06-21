# simter-jackson-javatime changelog

## 0.6.0 - 2022-06-21

- Upgrade to simter-dependencies-3.0.0 (jdk-17)

## 0.5.0 - 2020-12-03

- Upgrade to simter-dependencies-2.0.0
- Fixed multiple instance error on JavaTimeModule

## 0.4.0 - 2020-07-28

- Upgrade to simter-dependencies-2.0.0-M2
- Support config the global java-time serialize format

## 0.3.0 - 2019-07-03

No code changed, just polishing maven config and unit test.

- Use json-unit-assertj instead of json-path-assert
- Change parent to simter-dependencies-1.2.0

## 0.2.0 - 2019-05-13

- Only expose JavaTimeModule API
- Add services config for com.fasterxml.jackson.databind.Module
- Refactor package name `tech.simter.jackson.ext.javatime` to 'tech.simter.jackson.javatime'
- Convert source code from kotlin to java for compatibility
- Delete slf4j compile dependency
- Delete unnecessary test case

## 0.1.0 - 2019-05-13

> This version is same with [simter-jackson-ext-1.0.0], just rename artifactId to `simter-jackson-javatime`.

- Copy source code from [simter-jackson-ext].
- Initial global default format :

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


[simter-jackson-ext]: https://github.com/simter/simter-jackson-ext
[simter-jackson-ext-1.0.0]: https://github.com/simter/simter-jackson-ext/tree/1.0.0