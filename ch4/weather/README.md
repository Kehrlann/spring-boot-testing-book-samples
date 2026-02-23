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

### API tests

The following tests verify the API endpoints:

- **`ApiTests`** - Uses `MockMvcTester`. Demonstrates HTML and JSON response assertions, JSON path extraction, lenient
  JSON comparison with custom comparators, deserialization into records, and `RequestPostProcessor` usage.
- **`ApiMockMvcTests`** - Uses the "legacy" `MockMvc` API with Hamcrest matchers. Provides a side-by-side comparison
  with `ApiTests` to contrast the two MockMvc styles.
- **`ApiRestTestClientTests`** - Uses `RestTestClient`, an alternative to MockMvc that uses a `RestClient`-style API
  while still running requests in-process.
- **`ApiWebClientTests`** - Uses `WebTestClient`, the reactive alternative to MockMvc that uses a `WebClient`-style API.
- **`TomcatTests`** - Contrasts MockMvc (no servlet container) with a full Tomcat server (`RANDOM_PORT`). Demonstrates
  that MockMvc does not go through Tomcat (e.g., no access logs, no custom error pages), while the full server does, and
  shows how to use `@LocalServerPort` and `RestTestClient` for full webserver tests.
- **`MockMvcInternalsTests`** - Illustrates a key difference between MockMvc and a real servlet container: MockMvc runs
  requests on the same thread as the test, so `ThreadLocal` values are shared, whereas Tomcat processes requests on a
  separate thread.

### Browser tests

- **`HtmlUnitTests`** - Tests the full HTML pages and JavaScript interactions using HtmlUnit, an in-process headless
  browser. Demonstrates DOM querying, form input, keyboard events, waiting for background JavaScript, and the
  limitations of HtmlUnit's JavaScript engine with modern syntax.
- **`SeleniumTests`** - End-to-end browser tests using Selenium WebDriver with a headless Chrome instance. Demonstrates
  managing the browser lifecycle with `@BeforeAll`/`@AfterAll`, testing autocomplete, keyboard and mouse interactions,
  and waiting for dynamic DOM updates.
- **`SeleniumDriverAsBeanTests`** - A variant of `SeleniumTests` that registers the `ChromeDriver` as a Spring bean in a
  `@TestConfiguration`, rather than using static lifecycle methods.
