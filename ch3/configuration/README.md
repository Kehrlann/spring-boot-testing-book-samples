# Chapter 3: Configuring the application for testing

## The application

The application under test is a simple command-line program that configures beans and shows their names, then exits.
Run it with:

```shell
./mvnw spring-boot:run
```

## Test classes

### Configuration tests

- **`DemoApplicationTests`** - Default test verifying the application context loads with the expected beans, based on
  Spring Initializr-generated tests.
- **`InteractWithAppTests`** - Demonstrates ways to interact with the application context in tests.
- **`PropertiesTests`** - Demonstrates multiple ways to override application properties in tests.
- **`TestConfigurationTests`** - Showcases `@TestConfiguration` usage, adding beans via a nested static
  `@TestConfiguration` class, importing an external `@TestConfiguration` with `@Import`, and verifying that
  `@TestConfiguration` adds to (rather than replaces) the existing application configuration.
- **`CustomConfigTests`** - Covers advanced configuration scenarios: using `classes = {...}` to limit the application
  context to specific configuration classes, `@MockitoBean` and `@TestBean` for overriding beans, nested
  `@Configuration` classes (which replace the entire application context), and `@Import` to add configurations
  alongside the default component scan.
- **`ProfileTests`** - Activates a custom Spring profile using `@ActiveProfiles("custom")` and verifies that
  profile-specific beans are loaded.

### Context cache tests

- **`SlowApplicationTests`** - Demonstrates Spring's test application context caching behavior. Uses a deliberately
  slow-starting application to show when contexts are reused vs. recreated.
- **`CustomSpringTest`** - A custom composed annotation combining `@SpringBootTest`, `@MockitoBean`, and custom
  properties into a single reusable annotation, demonstrating how to standardize test configuration.
