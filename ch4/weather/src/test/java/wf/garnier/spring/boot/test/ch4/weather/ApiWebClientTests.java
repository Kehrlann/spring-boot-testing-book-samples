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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
class ApiWebClientTests {

	@Autowired
	private WebTestClient client;

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

	@Test
	void indexPageLoads() {
		client.get()
			.uri("/")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody(String.class)
			.value(body -> assertThat(body).contains("<h1>Weather App</h1>"));
	}

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

		var body = "{ \"id\": %s }".formatted(paris.getId());

		client.method(org.springframework.http.HttpMethod.DELETE)
			.uri("/api/city")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus()
			.isNoContent();

		assertThat(selectionRepository.count()).isEqualTo(0);
	}

	@Test
	void unselectMissingCity() {
		var body = "{ \"id\": %s }".formatted(paris.getId());

		client.method(org.springframework.http.HttpMethod.DELETE)
			.uri("/api/city")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus()
			.isNoContent();

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

	private City selectCity(String name) {
		var city = this.cityRepository.findByNameIgnoreCase(name).get();
		selectionRepository.save(new Selection(city));
		return city;
	}

}