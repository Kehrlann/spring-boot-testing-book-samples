package wf.garnier.spring.boot.test.ch2.weather;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch2.weather.city.City;
import wf.garnier.spring.boot.test.ch2.weather.city.CityRepository;
import wf.garnier.spring.boot.test.ch2.weather.openmeteo.WeatherData;
import wf.garnier.spring.boot.test.ch2.weather.openmeteo.WeatherService;
import wf.garnier.spring.boot.test.ch2.weather.selection.CityWeather;
import wf.garnier.spring.boot.test.ch2.weather.selection.Selection;
import wf.garnier.spring.boot.test.ch2.weather.selection.SelectionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class ExampleTests {

	@Autowired
	private MockMvcTester mvc;

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
	void contextLoads() {

	}

	@Test
	void indexPageLoads() {
		//@formatter:off
		// 1. Arrange
		// We do not need to set up anything in particular

		// 2. Act
		var response = mvc.get()
				.uri("/")
				.exchange();

		// 3. Assert
		assertThat(response)
				.hasStatus(HttpStatus.OK)
				.bodyText()
				.contains("<h1>Weather App</h1>");
		//@formatter:on
	}

	@Test
	void addSelectedCity() {
		//@formatter:off
		mvc.post()
				.uri("/city/add")
				.param("city", "Paris")
				.exchange();
		//@formatter:on

		assertThat(selectionRepository.findAll()).hasSize(1)
			.first()
			.extracting(Selection::getCity)
			.extracting(City::getName)
			.isEqualTo("Paris");
	}

	@Test
	void addSelectedCityApi() {
		//@formatter:off
		var response = mvc.post()
				.uri("/api/city")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\": \"%s\"}".formatted(paris.getId()))
				.exchange();
		//@formatter:on

		assertThat(response).hasStatus(HttpStatus.CREATED);

		assertThat(selectionRepository.findAll()).hasSize(1)
			.first()
			.extracting(Selection::getCity)
			.extracting(City::getName)
			.isEqualTo("Paris");
	}

	@Test
	void indexPageWithCity() {
		selectCity("Paris");

		//@formatter:off
		var response = mvc.get()
				.uri("/")
				.exchange();
		assertThat(response)
				.hasStatus(HttpStatus.OK)
				.bodyText()
				.contains("Paris");
		//@formatter:on
	}

	@Test
	void selectUnknown() {
		var response = mvc.post()
			.uri("/api/city")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"cityName\": \"" + "Foobar" + "\"}")
			.exchange();

		assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);

		assertThat(selectionRepository.count()).isEqualTo(0);
	}

	@Test
	void noDuplicates() {
		selectCity("Bogotá");
		selectCity("Bogotá");

		assertThat(selectionRepository.count()).isEqualTo(1);
	}

	@Test
	void getWeather() {
		when(weatherService.getCurrentWeather(anyDouble(), anyDouble())).thenReturn(new WeatherData(22.6, 0, 1));
		selectCity("Paris");

		var response = mvc.get().uri("/api/weather").exchange();

		// ... assertions omitted for brevity ...
		//@formatter:off
		assertThat(response)
				.hasStatus(HttpStatus.OK)
				.bodyJson()
				.isLenientlyEqualTo("""
						[
						  {
							"cityName": "Paris",
							"temperature": 22.6
						  }
						]
						""");
		//@formatter:on
	}

	@Test
	void getWeatherUsingJavaClass() {
		selectCity("Paris");

		var response = mvc.get().uri("/api/weather").exchange();

		assertThat(response).hasStatus(HttpStatus.OK)
			.bodyJson()
			.convertTo(list(CityWeather.class))
			.hasSize(1)
			.first()
			.hasFieldOrPropertyWithValue("cityName", "Paris")
			.hasFieldOrPropertyWithValue("temperature", 20.0);
	}

	@Test
	void getWeatherMultipleCities() {
		selectCity("Beijing");
		selectCity("Paris");

		var response = mvc.get().uri("/api/weather").exchange();
		assertThat(response).hasStatus(HttpStatus.OK);

		assertThat(response).bodyJson()
			.extractingPath("$.[*].cityName")
			.asArray()
			.hasSize(2)
			.containsExactlyInAnyOrder("Beijing", "Paris");
	}

	@Test
	void getWeatherMultipleCitiesOrderAlphabetically() {
		selectCity("Beijing");
		selectCity("Tokyo");
		selectCity("Quito");

		var response = mvc.get().uri("/api/weather").exchange();
		assertThat(response).hasStatus(HttpStatus.OK);

		assertThat(response).bodyJson()
			.hasPathSatisfying("$.[0].cityName", value -> assertThat(value).isEqualTo("Beijing"))
			.hasPathSatisfying("$.[1].cityName", value -> assertThat(value).isEqualTo("Quito"))
			.hasPathSatisfying("$.[2].cityName", value -> assertThat(value).isEqualTo("Tokyo"));
	}

	@Test
	void deleteCity() {
		selectCity("Paris");

		var response = mvc.delete()
			.uri("/api/city")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"id\": \"%s\"}".formatted(paris.getId()))
			.exchange();

		assertThat(response).hasStatus(HttpStatus.NO_CONTENT);
		assertThat(selectionRepository.count()).isEqualTo(0);
	}

	private MvcTestResult selectCity(String cityName) {
		//@formatter:off
		return mvc.post()
			.uri("/city/add")
			.param("city", cityName)
			.exchange();
		//@formatter:on
	}

}
