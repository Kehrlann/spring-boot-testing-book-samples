# Chapter 4: Testing Web Application

## The application

The application under test is a "weather" app, similar to chapter 4.
It is an upgraded version, using a full JavaScript frontend.
It has been split into "modules" using Spring Modulith.
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

## Test classes

### Architecture tests

- **`ModularityTests`** - Verifies the Spring Modulith structure and generates module documentation.

### API tests

- **`CityApiTests`** - Tests city API endpoints within the city module slice using `@ApplicationModuleTest`.
- **`CityControllerTests`** - Tests the city controller using a standard `@WebMvcTest` that loads all controllers globally.
- **`CityControllersTests`** - Tests the city controller using `@WebMvcTest` by explicitly defining the controller class to load.
- **`CityModuleControllersTests`** - Tests the city module controllers using `@WebMvcTest` combined with `@ModuleSlicing`.
- **`PreferencesApiTests`** - Tests preferences API endpoints within the preferences module slice using `@ApplicationModuleTest`.
- **`WeatherApiTests`** - Tests weather API endpoints within the weather module slice using `@ApplicationModuleTest`.

### Data and external service tests

- **`OpenMeteoWeatherDataServiceTest`** - Tests external HTTP calls to the Open-Meteo API using `@RestClientTest` and `MockRestServiceServer`.
- **`SelectedCityRepositoryTest`** - Tests database interactions and custom queries for selected cities using `@DataJpaTest`.

### Browser tests

- **`HtmlUnitTests`** - Tests the full HTML pages and JavaScript interactions using HtmlUnit, an in-process headless browser.
