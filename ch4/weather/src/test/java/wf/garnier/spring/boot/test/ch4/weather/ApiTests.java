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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class ApiTests {

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
	void selectCity() {
		var response = mvc.post().uri("/api/city").contentType(MediaType.APPLICATION_JSON).content("""
				{ "id": %s }
				""".formatted(paris.getId())).exchange();

		var cities = selectionRepository.findAll();
		assertThat(response).hasStatus(HttpStatus.CREATED);
		assertThat(cities).hasSize(1);
		assertThat(cities.get(0).getCity().getName()).isEqualTo("Paris");
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

		var response = mvc.delete().uri("/api/city").contentType(MediaType.APPLICATION_JSON).content("""
				{ "id": %s }
				""".formatted(paris.getId())).exchange();

		assertThat(response).hasStatus(HttpStatus.NO_CONTENT);
		assertThat(selectionRepository.count()).isEqualTo(0);
	}

	@Test
	void unselectMissingCity() {
		var response = mvc.delete().uri("/api/city").contentType(MediaType.APPLICATION_JSON).content("""
				{ "id": %s }
				""".formatted(paris.getId())).exchange();

		assertThat(response).hasStatus(HttpStatus.NO_CONTENT);
		assertThat(selectionRepository.count()).isEqualTo(0);
	}

	@Test
	void getWeather() {
		selectCity("Paris");

		var response = mvc.get().uri("/api/weather").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
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
	void getWeatherNoCity() {
		var response = mvc.get().uri("/api/weather").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().isEqualTo("[]");
	}

	private void selectCity(String name) {
		var city = this.cityRepository.findByNameIgnoreCase(name).get();
		selectionRepository.save(new Selection(city));
	}

}