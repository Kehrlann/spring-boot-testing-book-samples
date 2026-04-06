# Chapter 4: Testing Web Application

## The application

The application under test is a "weather" app, similar to chapter 2.
It is an upgraded version, with some javascript.
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

- **`ApiTests`** - Tests API endpoints. Uses `MockMvcTester`. Demonstrates HTML and JSON response assertions, JSON path
  extraction, lenient JSON comparison with custom comparators, deserialization into records, and `RequestPostProcessor`
  usage.
- **`HtmlUnitTests`** - Tests the full HTML pages and JavaScript interactions using HtmlUnit, an in-process headless
  browser. Demonstrates DOM querying, form input, keyboard events, and waiting for background JavaScript.
