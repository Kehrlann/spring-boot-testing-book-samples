package wf.garnier.spring.boot.test.ch5.weather.weather;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.servlet.http.Cookie;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;
import tools.jackson.core.type.TypeReference;
import tools.jackson.dataformat.xml.XmlMapper;
import wf.garnier.spring.boot.test.ch5.weather.selection.City;
import wf.garnier.spring.boot.test.ch5.weather.selection.CityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonAssert;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class ApiTests {

	@Autowired
	MockMvcTester mvc;

	@Autowired
	CityService cityService;

	@MockitoBean
	WeatherDataService weatherDataService;

	City paris;

	@BeforeEach
	void clearRepository() {
		// clean up existing selection
		cityService.getSelectedCities().forEach(c -> cityService.unselectCityById(c.getId()));
		paris = cityService.searchUnselectedCities("paris")
			.stream()
			.findFirst()
			.get();
	}

	@BeforeEach
	void configureMocks() {
		when(weatherDataService.getCurrentWeather(anyDouble(), anyDouble())).thenReturn(new WeatherData(20, 0, 0));
	}

	@Test
	void indexPageLoads() {
		var response = mvc.get().uri("/").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("<h1>Weather App</h1>");
	}

	@Test
	void indexPageHasSelectedCity() {
		selectCity("Paris");

		var response = mvc.get().uri("/").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("Paris (France)");
	}

	@Test
	void indexPageHasSelectedCityFluent() {
		selectCity("Paris");

		mvc.get().uri("/").exchange().assertThat().hasStatus(HttpStatus.OK).bodyText().contains("Paris (France)");
	}

	@Test
	void selectCity() {
		var response = mvc.post()
			.uri("/api/city")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{ \"id\": %s }".formatted(paris.getId()))
			.exchange();

		assertThat(response).hasStatus(HttpStatus.CREATED).body().isEmpty();
		var cities = cityService.getSelectedCities();

		assertThat(cities).hasSize(1).first().extracting(City::getName).isEqualTo("Paris");
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

		var cities = cityService.getSelectedCities();

		assertThat(cities).hasSize(1).first().extracting(City::getName).isEqualTo("Paris");
	}

	@Test
	void selectCityTwice() {
		mvc.post().uri("/api/city").contentType(MediaType.APPLICATION_JSON).content("""
				{ "id": %s }
				""".formatted(paris.getId())).exchange().assertThat().hasStatus(HttpStatus.CREATED);
		mvc.post().uri("/api/city").contentType(MediaType.APPLICATION_JSON).content("""
				{ "id": %s }
				""".formatted(paris.getId())).exchange().assertThat().hasStatus(HttpStatus.CONFLICT);

		assertThat(cityService.getSelectedCities()).hasSize(1);
	}

	@Test
	void unselectCity() {
		selectCity("Paris");

		var response = mvc.delete().uri("/api/city/{id}", paris.getId()).exchange();

		assertThat(response).hasStatus(HttpStatus.NO_CONTENT);
		assertThat(cityService.getSelectedCities()).isEmpty();
	}

	@Test
	void unselectMissingCity() {
		var response = mvc.delete().uri("/api/city/{id}", paris.getId()).exchange();

		assertThat(response).hasStatus(HttpStatus.NO_CONTENT);
		assertThat(cityService.getSelectedCities()).isEmpty();
	}

	@Test
	void getWeather() {
		var paris = selectCity("Paris");

		var response = mvc.get().uri("/api/weather").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
				[
				  {
				    "cityName": "Paris",
				    "country": "France",
				    "weather": "Clear sky",
				    "temperature": 20.0,
				    "windSpeed": 0.0
				  }
				]
				""").extractingPath("$.[0].cityId").isEqualTo(paris.getId());
		assertThat(response).bodyJson()
			.convertTo(InstanceOfAssertFactories.list(WeatherResponse.class))
			.hasSize(1)
			.first()
			.satisfies(wr -> {
				assertThat(wr.cityName()).isEqualTo("Paris");
				assertThat(wr.temperature()).isCloseTo(20, withPercentage(10));
			});
		assertThat(response).bodyJson().extractingPath("$.[0]").convertTo(WeatherResponse.class).satisfies(wr -> {
			assertThat(wr.cityName()).isEqualTo("Paris");
			assertThat(wr.temperature()).isCloseTo(20, withPercentage(10));
		});
	}

	/**
	 * {@link MockMvcTester} only understands bytes, strings or JSON as a response. To
	 * read any other format, including XML, you must deserialize it yourself.
	 */
	@Test
	void getWeatherXml() throws IOException {
		selectCity("Paris");

		var response = mvc.get().uri("/api/weather").accept(MediaType.APPLICATION_XML).exchange();

		assertThat(response).hasStatus2xxSuccessful();

		var xmlBody = response.getResponse().getContentAsByteArray();
		var xmlMapper = XmlMapper.builder().build();
		var body = xmlMapper.readValue(xmlBody, new TypeReference<List<WeatherResponse>>() {
		}); // yuck

		assertThat(body).hasSize(1).first().satisfies(wr -> {
			assertThat(wr.cityName()).isEqualTo("Paris");
			assertThat(wr.temperature()).isCloseTo(20, withPercentage(10));
		});
	}

	@Test
	void getWeatherMultipleCities() {
		var lagos = selectCity("Lagos");
		var shenzhen = selectCity("Shenzhen");
		when(weatherDataService.getCurrentWeather(anyDouble(), anyDouble())).thenReturn(new WeatherData(25, 0, 0))
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

	/**
	 * You can use the {@link CustomComparator} to apply custom evaluation conditions.
	 * Here, we are ignoring the cityId by path, but also
	 */
	@Test
	void getWeatherMultipleCitiesCustomizations() {
		selectCity("Lagos");
		selectCity("Shenzhen");

		when(weatherDataService.getCurrentWeather(anyDouble(), anyDouble())).thenReturn(new WeatherData(25, 0, 0))
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
				""", JsonAssert.comparator(ignoringValues("?")));
	}

	private static CustomComparator ignoringPaths(String... path) {
		var customizations = Arrays.stream(path)
			.map(p -> new Customization(p, (actual, expected) -> true))
			.toArray(Customization[]::new);
		return new CustomComparator(JSONCompareMode.LENIENT, customizations);
	}

	private static JSONComparator ignoringValues(String... values) {
		return new DefaultComparator(JSONCompareMode.LENIENT) {
			@Override
			public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result)
					throws JSONException {
				if (Arrays.asList(values).contains(expectedValue.toString())) {
					return;
				}
				super.compareValues(prefix, expectedValue, actualValue, result);
			}
		};
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

	private static RequestPostProcessor apiUser(String username) {
		return (MockHttpServletRequest request) -> {
			request.addHeader("accept", "application/vnd.api+json");
			request.addHeader("x-answer ", "42");
			request.setCookies(new Cookie("user", username));
			return request;
		};
	}

	private City selectCity(String name) {
		var city = cityService.searchUnselectedCities(name)
			.stream()
			.filter(c -> c.getName().equalsIgnoreCase(name))
			.findFirst()
			.get();
		cityService.addCityById(city.getId());
		return city;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record WeatherResponse(String cityName, String country, Double temperature) {
	}

}
