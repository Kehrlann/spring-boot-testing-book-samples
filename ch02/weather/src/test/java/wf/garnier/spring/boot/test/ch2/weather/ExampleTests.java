package wf.garnier.spring.boot.test.ch2.weather;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch2.weather.city.City;
import wf.garnier.spring.boot.test.ch2.weather.openmeteo.WeatherData;
import wf.garnier.spring.boot.test.ch2.weather.openmeteo.WeatherService;
import wf.garnier.spring.boot.test.ch2.weather.selection.CityWeather;
import wf.garnier.spring.boot.test.ch2.weather.selection.Selection;
import wf.garnier.spring.boot.test.ch2.weather.selection.SelectionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

// tag::annotations[]
@SpringBootTest // <1>
@AutoConfigureMockMvc // <2>
// end::annotations[]
// tag::class[]
class ExampleTests {

	// end::class[]
	// tag::mock-mvc-tester[]
	@Autowired // <2>
	private MockMvcTester mvc; // <2>

	// end::mock-mvc-tester[]

	// tag::selection-repository[]
	@Autowired // <1>
	private SelectionRepository selectionRepository; // <1>

	// end::selection-repository[]

	// tag::mockito-bean[]
	@MockitoBean
	private WeatherService weatherService; // <1>

	// end::mockito-bean[]

	// tag::before-each[]
	@BeforeEach
	void clearRepository() {
		selectionRepository.deleteAll();
	}
	// end::before-each[]

	@BeforeEach
	void configureMocks() {
		// Note: this can be folded into a single @BeforeEach method
		when(weatherService.getCurrentWeather(anyDouble(), anyDouble())).thenReturn(new WeatherData(20, 0, 0));
	}

	@Test
	void contextLoads() {

	}

	// tag::first-test[]
	@Test
	void indexPageLoads() {
		//@formatter:off
		// 1. Arrange
		// We do not need to set up anything in particular

		// 2. Act
		var response = mvc.get() // <3>
				.uri("/")
				.exchange();

		// 3. Assert
		assertThat(response) // <4>
				.hasStatus(HttpStatus.OK)
				.bodyText()
				.contains("<h1>Weather App</h1>");
		//@formatter:on
	}
	// end::first-test[]

	// tag::selection-test[]
	@Test
	void addSelectedCity() {
		//@formatter:off
		mvc.post()
				.uri("/city/add")
				.param("city", "Paris")
				.exchange();
		//@formatter:on

		assertThat(selectionRepository.findAll()).hasSize(1) // <2>
			.first()
			.extracting(Selection::getCity)
			.extracting(City::getName)
			.isEqualTo("Paris");
	}
	// end::selection-test[]

	@Test
	void addSelectedCityApi() {
		//@formatter:off
		var response = mvc.post()
				.uri("/api/city")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cityName\": \"Paris\"}")
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

	// tag::no-duplicates[]
	@Test
	void noDuplicates() {
		selectCity("Bogotá");
		selectCity("Bogotá");

		assertThat(selectionRepository.count()).isEqualTo(1);
	}
	// end::no-duplicates[]

	// tag::weather-data[]
	@Test
	void getWeather() {
		// tag::weather-data-mock[]
		when(weatherService.getCurrentWeather(anyDouble(), anyDouble())) // <2>
			.thenReturn(new WeatherData(22.6, 0, 1)); // <2>
		// end::weather-data-mock[]
		selectCity("Paris");

		var response = mvc.get().uri("/api/weather").exchange();

		// tag::weather-data-mock[]
		// ... assertions omitted for brevity ...
		// end::weather-data-mock[]
		// tag::weather-data-assertions[]
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
		// end::weather-data-assertions[]
	}
	// end::weather-data[]

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
			.content("{\"cityName\": \"Paris\"}")
			.exchange();

		assertThat(response).hasStatus(HttpStatus.NO_CONTENT);
		assertThat(selectionRepository.count()).isEqualTo(0);
	}

	// tag::select-city[]
	private MvcTestResult selectCity(String cityName) {
		//@formatter:off
		return mvc.post()
			.uri("/city/add")
			.param("city", cityName)
			.exchange();
		//@formatter:on
	}
	// end::select-city[]

	// tag::class[]

}
// end::class[]
