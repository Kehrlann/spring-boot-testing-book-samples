package wf.garnier.spring.boot.test.ch4.weather;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import wf.garnier.spring.boot.test.ch4.weather.city.City;
import wf.garnier.spring.boot.test.ch4.weather.city.CityRepository;
import wf.garnier.spring.boot.test.ch4.weather.openmeteo.WeatherData;
import wf.garnier.spring.boot.test.ch4.weather.openmeteo.WeatherService;
import wf.garnier.spring.boot.test.ch4.weather.selection.Selection;
import wf.garnier.spring.boot.test.ch4.weather.selection.SelectionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonAssert;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

// tag::class[]
@SpringBootTest
@AutoConfigureMockMvc // <1>
class ApiTests {

	// end::class[]
	// tag::mock-mvc-tester[]
	//@formatter:off
	@Autowired MockMvcTester mvc; // <1>
	//@formatter:on

	// end::mock-mvc-tester[]

	@Autowired
	SelectionRepository selectionRepository;

	@MockitoBean
	WeatherService weatherService;

	@Autowired
	CityRepository cityRepository;

	City paris;

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
		//@formatter:off
		// Extract the response into a variable
		var response = mvc.get()
			.uri("/")
			.exchange();

		assertThat(response)
			.hasStatus(HttpStatus.OK)
			.bodyText().contains("<h1>Weather App</h1>");
		//@formatter:on
	}

	// tag::index-page[]
	@Test
	void indexPageHasSelectedCity() {
		selectCity("Paris");
		// Extract the response into a variable
		var response = mvc.get() // <2>
			.uri("/") // <3>
			.exchange(); // <4>

		// Assert statement-by-statement
		assertThat(response).hasStatus(HttpStatus.OK); // <5>
		assertThat(response).bodyText().contains("Paris (France)"); // <6>

		// Same assertions, but fluent-style
		//@formatter:off
		assertThat(response).hasStatus(HttpStatus.OK) // <5>
			.bodyText().contains("Paris (France)"); // <6>
		//@formatter:on
	}
	// end::index-page[]

	// tag::fluent-assertions[]
	@Test
	void indexPageHasSelectedCityFluent() {
		selectCity("Paris");

		//@formatter:off
		mvc.get()
				.uri("/")
				.exchange()
				.assertThat()
				.hasStatus(HttpStatus.OK)
				.bodyText()
				.contains("Paris (France)");
		//@formatter:on
	}
	// end::fluent-assertions[]

	@Test
	void selectCity() {
		var response = mvc.post()
			.uri("/api/city")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{ \"id\": %s }".formatted(paris.getId()))
			.exchange();

		assertThat(response).hasStatus(HttpStatus.CREATED).body().isEmpty();
		var cities = selectionRepository.findAll();

		assertThat(cities).hasSize(1)
			.first()
			.extracting(Selection::getCity)
			.extracting(City::getName)
			.isEqualTo("Paris");
	}

	@Test
	void selectCityAlternative() {
		mvc.post()
			.uri("/api/city")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{ \"id\": %s }".formatted(paris.getId()))
			.exchange()
			.assertThat()
			.hasStatus(HttpStatus.CREATED)
			.body()
			.isEmpty();
	}

	@Test
	void selectCityTwice() {
		mvc.post().uri("/api/city").contentType(MediaType.APPLICATION_JSON).content("""
				{ "id": %s }
				""".formatted(paris.getId())).exchange().assertThat().hasStatus(HttpStatus.CREATED);
		mvc.post().uri("/api/city").contentType(MediaType.APPLICATION_JSON).content("""
				{ "id": %s }
				""".formatted(paris.getId())).exchange().assertThat().hasStatus(HttpStatus.CONFLICT);

		assertThat(selectionRepository.count()).isEqualTo(1);
	}

	@Test
	void unselectCity() {
		selectCity("Paris");

		//@formatter:off
		var response = mvc.delete()
			.uri("/api/city/{id}", paris.getId())
			.exchange();
		//@formatter:on

		assertThat(response).hasStatus(HttpStatus.NO_CONTENT);
		assertThat(selectionRepository.count()).isEqualTo(0);
	}

	@Test
	void unselectMissingCity() {
		var response = mvc.delete().uri("/api/city/{id}", paris.getId()).exchange();

		assertThat(response).hasStatus(HttpStatus.NO_CONTENT);
		assertThat(selectionRepository.count()).isEqualTo(0);
	}

	// tag::api-test[]
	@Test
	void getWeather() {
		var paris = selectCity("Paris");

		var response = mvc.get().uri("/api/weather").exchange();

		//@formatter:off
		assertThat(response)
			.hasStatus(HttpStatus.OK)
			.bodyJson().isLenientlyEqualTo(
					"""
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
					""".formatted(paris.getId())
			);
		//@formatter:on
	}
	// end::api-test[]

	@Test
	void getWeatherMultipleCities() {
		var lagos = selectCity("Lagos");
		var shenzhen = selectCity("Shenzhen");
		when(weatherService.getCurrentWeather(anyDouble(), anyDouble())).thenReturn(new WeatherData(25, 0, 0))
			.thenReturn(new WeatherData(17, 5, 1));

		var response = mvc.get().uri("/api/weather").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
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
	void getWeatherMultipleCitiesCustomizations() {
		selectCity("Lagos");
		selectCity("Shenzhen");

		when(weatherService.getCurrentWeather(anyDouble(), anyDouble())).thenReturn(new WeatherData(25, 0, 0))
			.thenReturn(new WeatherData(17, 5, 1));

		var response = mvc.get().uri("/api/weather").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().isEqualTo("""
				[
				  {
				    "cityName": "Lagos",
				    "country": "Nigeria",
				    "cityId": ?,
				    "weather": "Clear sky",
				    "temperature": 25.0,
				    "windSpeed": 0.0
				  },
				  {
				    "cityName": "Shenzhen",
				    "country": "China",
				    "cityId": ?,
				    "weather": "Partly cloudy",
				    "temperature": 17.0,
				    "windSpeed": 5.0
				  }
				]
				""", JsonAssert.comparator(ignoringPaths("[*].cityId")));
	}

	private static CustomComparator ignoringPaths(String... path) {
		var customizations = Arrays.stream(path)
			.map(p -> new Customization(p, (actual, expected) -> true))
			.toArray(Customization[]::new);
		return new CustomComparator(JSONCompareMode.LENIENT, customizations);
	}

	@Test
	void getWeatherNoCity() {
		var response = mvc.get().uri("/api/weather").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().isEqualTo("[]");
	}

	@Test
	void cityAutocomplete() {
		var response = mvc.get().uri("/api/city").queryParam("q", "quito").exchange();

		assertThat(response).hasStatus(HttpStatus.OK)
			.bodyJson()
			.extractingPath("$.[*].name")
			.asArray()
			.containsOnly("Quito", "Iquitos");
	}

	@Test
	void cityAutocompleteEmpty() {
		var response = mvc.get().uri("/api/city").queryParam("q", "").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().extractingPath("$.length()").isEqualTo(4999);
	}

	@Test
	void cityAutocompleteNoMatch() {
		var response = mvc.get().uri("/api/city").queryParam("q", "r'lyeh").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().extractingPath("$.length()").isEqualTo(0);
	}

	private City selectCity(String name) {
		var city = this.cityRepository.findByNameIgnoreCase(name).get();
		selectionRepository.save(new Selection(city));
		return city;
	}

	// tag::class[]

}
// end::class[]
