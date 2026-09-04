# Chapter 6: Testing Configuration and Properties

## The application

The application under test is the "weather" app from chapter 5.
It is a Spring Modulith application, with a full JavaScript frontend.
Run it with:

```shell
./mvnw spring-boot:run
```

Access it via http://localhost:8080/.

The application uses the https://open-meteo.com API for getting weather data.
If you cannot access the API, you can run the app with the `local` profile and get random data instead:

```shell
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Configuration properties

Compared to chapter 5, the `preferences` module is now configurable through
`@ConfigurationProperties`, in `PreferencesProperties` (prefix: `preferences`):

- `preferences.defaults.dark-mode`, `preferences.defaults.units` and `preferences.defaults.sort-by`
  define the preferences given to a user who has never saved any.
- `preferences.temperature-threshold.cold` and `preferences.temperature-threshold.hot` are exposed
  to the frontend, which displays temperatures below `cold` in blue, and above `hot` in red.

The properties are validated in two ways:

- With Jakarta Bean Validation constraints (`@Min`, `@Max`) on `TemperatureThreshold`, activated by
  `@Validated` on the properties class and `@Valid` on the nested records.
- With custom logic in `afterPropertiesSet()` (`InitializingBean`), which rejects a `cold` threshold
  that is higher than the `hot` threshold.

The properties are registered with `@EnableConfigurationProperties` in `PreferencesConfiguration`,
which makes them easy to load in isolation in tests.

A full example of the supported configuration is in
[`application-custom.yml`](src/main/resources/application-custom.yml), which can be activated with:

```shell
./mvnw spring-boot:run -Dspring-boot.run.profiles=custom
```

## Test classes

### Configuration and properties tests

- **`PreferencesConfigurationTests`** - Shows the many ways to feed properties to a Spring test:
  `@SpringBootTest(properties = ...)`, `@TestPropertySource` (explicit locations, implicit
  `<TestClass>.properties` file, repeated with `@TestPropertySources`, and YAML files through a
  custom `PropertySourceFactory`), `@ActiveProfiles`, and a `SpringApplicationBuilder` built by
  hand. The `SpringApplicationBuilder` tests also assert that startup *fails* when properties are
  invalid, and show how to turn YAML into `Properties` with `YamlPropertiesFactoryBean` and
  `YamlPropertySourceLoader`.
- **`PreferencesPropertiesTests`** - Tests the validation of `PreferencesProperties` without any
  Spring context: constraints are checked with a `LocalValidatorFactoryBean` (and with a raw Jakarta
  `Validator`), and property objects are built either directly with their constructor or by
  deserializing YAML with a kebab-case `YAMLMapper`.
- **`YamlPropertySourceFactory`** - Test utility (not a test) that allows `@TestPropertySource` to
  load `yaml` files, which it does not support out of the box.

### Architecture tests

- **`ModularityTests`** - Verifies the Spring Modulith structure and generates module documentation.

### API tests

- **`CityApiTests`** - Tests city API endpoints within the city module slice using `@ApplicationModuleTest`.
- **`CityControllerTests`** - Tests the city controller using a standard `@WebMvcTest` that loads all controllers globally.
- **`CityControllersTests`** - Tests the city module controllers using `@WebMvcTest` combined with `@ModuleSlicing`.
- **`PreferencesApiTests`** - Tests preferences API endpoints within the preferences module slice using `@ApplicationModuleTest`.
- **`WeatherApiTests`** - Tests weather API endpoints within the weather module slice using `@ApplicationModuleTest`.

### Data and external service tests

- **`OpenMeteoWeatherDataServiceTests`** - Tests external HTTP calls to the Open-Meteo API using `@RestClientTest` and `MockRestServiceServer`.
- **`SelectedCityRepositoryTests`** - Tests database interactions and custom queries for selected cities using `@DataJpaTest`.

### Browser tests

- **`HtmlUnitTests`** - Tests the full HTML pages and JavaScript interactions using HtmlUnit, an in-process headless browser.
