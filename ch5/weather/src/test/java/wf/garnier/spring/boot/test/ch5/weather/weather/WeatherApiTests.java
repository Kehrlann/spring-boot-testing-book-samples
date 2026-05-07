package wf.garnier.spring.boot.test.ch5.weather.weather;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.dataformat.xml.XmlMapper;
import wf.garnier.spring.boot.test.ch5.weather.city.City;
import wf.garnier.spring.boot.test.ch5.weather.city.CityService;
import wf.garnier.spring.boot.test.ch5.weather.city.internal.CityRepository;
import wf.garnier.spring.boot.test.ch5.weather.city.internal.SelectedCityRepository;
import wf.garnier.spring.boot.test.ch5.weather.weather.internal.WeatherDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class WeatherApiTests {

	@Autowired
	MockMvcTester mvc;

	@Autowired
	CityService cityService;

	@Autowired
	SelectedCityRepository selectedCityRepository;

	@Autowired
	CityRepository cityRepository;

	@MockitoBean
	WeatherDataService weatherDataService;

	City paris;

	@BeforeEach
	void clearRepository() {
		selectedCityRepository.deleteAll();
		paris = cityRepository.findByNameIgnoreCase("paris").get();
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
	void getWeatherXml() {
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

	@Test
	void getWeatherNoCity() {
		var response = mvc.get().uri("/api/weather").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().isEqualTo("[]");
	}

	private City selectCity(String name) {
		var city = cityRepository.findByNameIgnoreCase(name).get();
		cityService.addCityById(city.getId());
		return city;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record WeatherResponse(String cityName, String country, Double temperature) {
	}

}
