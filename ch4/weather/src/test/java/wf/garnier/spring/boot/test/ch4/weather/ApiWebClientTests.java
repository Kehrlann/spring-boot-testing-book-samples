package wf.garnier.spring.boot.test.ch4.weather;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch4.weather.city.City;
import wf.garnier.spring.boot.test.ch4.weather.city.CityRepository;
import wf.garnier.spring.boot.test.ch4.weather.openmeteo.WeatherData;
import wf.garnier.spring.boot.test.ch4.weather.openmeteo.WeatherService;
import wf.garnier.spring.boot.test.ch4.weather.selection.Selection;
import wf.garnier.spring.boot.test.ch4.weather.selection.SelectionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.assertj.WebTestClientResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

// tag::class[]
@SpringBootTest
@AutoConfigureWebTestClient // <1>
class ApiWebClientTests {

	// end::class[]

	// tag::web-test-client[]
	@Autowired
	private WebTestClient client; // <2>

	// end::web-test-client[]
	@Autowired
	private SelectionRepository selectionRepository;

	@MockitoBean
	private WeatherService weatherService;

	@Autowired
	private CityRepository cityRepository;

	private City paris;

	@BeforeEach
	void clearRepository() {
		selectionRepository.deleteAll();
		paris = cityRepository.findByNameIgnoreCase("paris").get();
	}

	@BeforeEach
	void configureMocks() {
		// Note: this can be folded into a single @BeforeEach method
		when(weatherService.getCurrentWeather(anyDouble(), anyDouble())).thenReturn(new WeatherData(20, 0, 0));
	}

	// tag::test[]
	@Test
	void indexPageLoads() {
		//@formatter:off
		client.get()
			.uri("/")
			.exchange() // <3>
			.expectStatus() // <4>
			.isOk() // <4>
			.expectBody(String.class) // <4>
			.value(body -> // <4>
                assertThat(body).contains("<h1>Weather App</h1>") // <4>
            );
		//@formatter:on
	}
	// end::test[]

	// tag::test-assertj[]
	@Test
	void assertjVariant() {
		var webClientResponse = client.get().uri("/").exchange(); // <1>
		var response = WebTestClientResponse.from(webClientResponse); // <2>
		assertThat(response).hasStatusOk() // <3>
			.bodyText()
			.contains("<h1>Weather App</h1>");
	}
	// end::test-assertj[]

	@Test
	void indexPageHasSelectedCity() {
		selectCity("Paris");

		client.get()
			.uri("/")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody(String.class)
			.value(body -> assertThat(body).contains("Paris (France)"));
	}

	@Test
	void selectCity() {
		var body = "{ \"id\": %s }".formatted(paris.getId());
		client.post()
			.uri("/api/city")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus()
			.isCreated();

		var cities = selectionRepository.findAll();
		assertThat(cities).map(Selection::getCity).map(City::getName).containsOnly("Paris");
	}

	@Test
	void selectCityTwice() {
		var body = "{ \"id\": %s }".formatted(paris.getId());

		client.post()
			.uri("/api/city")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus()
			.isCreated();

		client.post()
			.uri("/api/city")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus()
			.isEqualTo(HttpStatus.CONFLICT);

		assertThat(selectionRepository.count()).isEqualTo(1);
	}

	@Test
	void unselectCity() {
		selectCity("Paris");

		//@formatter:off
		client.delete()
			.uri("/api/city/{id}", paris.getId())
			.exchange()
			.expectStatus()
			.isNoContent();
		//@formatter:on

		assertThat(selectionRepository.count()).isEqualTo(0);
	}

	@Test
	void unselectMissingCity() {
		//@formatter:off
		client.delete()
			.uri("/api/city/{id}", paris.getId())
			.exchange()
			.expectStatus()
			.isNoContent();
		//@formatter:on

		assertThat(selectionRepository.count()).isEqualTo(0);
	}

	@Test
	void getWeather() {
		selectCity("Paris");

		client.get().uri("/api/weather").exchange().expectStatus().isOk().expectBody().json("""
				[
				  {
				    "cityName": "Paris",
				    "country": "France",
				    "cityId": %s,
				    "weather": "Clear sky",
				    "temperature": 20.0,
				    "windSpeed": 0.0
				  }
				]
				""".formatted(paris.getId()));
	}

	@Test
	void getWeatherMultipleCities() {
		var lagos = selectCity("Lagos");
		var shenzhen = selectCity("Shenzhen");
		when(weatherService.getCurrentWeather(anyDouble(), anyDouble())).thenReturn(new WeatherData(25, 0, 0))
			.thenReturn(new WeatherData(17, 5, 1));

		client.get().uri("/api/weather").exchange().expectStatus().isOk().expectBody().json("""
				[
				  {
				    "cityName": "Lagos",
				    "country": "Nigeria",
				    "cityId": %s,
				    "weather": "Clear sky",
				    "temperature": 25.0,
				    "windSpeed": 0.0
				  },
				  {
				    "cityName": "Shenzhen",
				    "country": "China",
				    "cityId": %s,
				    "weather": "Partly cloudy",
				    "temperature": 17.0,
				    "windSpeed": 5.0
				  }
				]
				""".formatted(lagos.getId(), shenzhen.getId()));
	}

	@Test
	void getWeatherNoCity() {
		client.get().uri("/api/weather").exchange().expectStatus().isOk().expectBody().json("[]");
	}

	@Test
	void cityAutocomplete() {
		client.get()
			.uri(uriBuilder -> uriBuilder.path("/api/city").queryParam("q", "quito").build())
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.jsonPath("$.[*].name")
			.value(names -> assertThat(names)
				.asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.list(String.class))
				.containsOnly("Quito", "Iquitos"));
	}

	@Test
	void cityAutocompleteEmpty() {
		client.mutate()
			.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
			.build()
			.get()
			.uri(uriBuilder -> uriBuilder.path("/api/city").queryParam("q", "").build())
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.jsonPath("$.length()")
			.isEqualTo(4999);
	}

	@Test
	void cityAutocompleteNoMatch() {
		client.get()
			.uri(uriBuilder -> uriBuilder.path("/api/city").queryParam("q", "r'lyeh").build())
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody()
			.jsonPath("$.length()")
			.isEqualTo(0);
	}

	/// This test uses a _generic_ {@link WebTestClient} to call {@code manning.com}.
	/// It does **NOT** use the auto-configured, auto-wired instance, which points to
	/// the app under test.
	// tag::webtestclient-manning[]
	@Test
	void testManningWebsite() {
		var client = WebTestClient.bindToServer().build(); // <1>

		//@formatter:off
		client.get()
			.uri("https://www.manning.com/terms-of-use") // <2>
			.exchange() // <3>
			.expectStatus()
			.isOk()
			.expectBody(String.class)
			.value(b -> assertThat(b)
				.containsIgnoringCase("General terms of Use")
			);
		//@formatter:on
		// tag::ignored[]
		// You can also build a WebTestClient with a base-url, so you can call the path
		// directly.
		var manningClient = WebTestClient.bindToServer().baseUrl("https://www.manning.com").build();
		manningClient.get()
			.uri("/terms-of-use")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody(String.class)
			.value(b -> assertThat(b).containsIgnoringCase("General terms of Use"));
		// end::ignored[]
	}
	// end::webtestclient-manning[]

	private City selectCity(String name) {
		var city = this.cityRepository.findByNameIgnoreCase(name).get();
		selectionRepository.save(new Selection(city));
		return city;
	}

	// tag::class[]

}
// end::class[]