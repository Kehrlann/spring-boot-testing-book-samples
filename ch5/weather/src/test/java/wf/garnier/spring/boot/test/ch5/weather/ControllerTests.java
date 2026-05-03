package wf.garnier.spring.boot.test.ch5.weather;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch5.weather.city.CityAlreadySelectedException;
import wf.garnier.spring.boot.test.ch5.weather.city.CityNotFoundException;
import wf.garnier.spring.boot.test.ch5.weather.city.CityService;
import wf.garnier.spring.boot.test.ch5.weather.weather.WeatherService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@WebMvcTest
class ControllerTests {

	@Autowired
	private MockMvcTester mvc;

	@MockitoBean
	CityService cityService;

	@MockitoBean
	WeatherService weatherService;

	@BeforeEach
	void setUp() {
		reset(cityService);
	}

	@Test
	void selectCity() {
		var response = mvc.post()
			.uri("/api/city")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{ \"id\": 42 }")
			.exchange();

		assertThat(response).hasStatus(HttpStatus.CREATED).body().isEmpty();
		verify(cityService).addCityById(42);
	}

	@Test
	void cityDoesNotExist() {
		doThrow(new CityNotFoundException(42)).when(cityService).addCityById(anyLong());
		var response = mvc.post()
			.uri("/api/city")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{ \"id\": 42 }")
			.exchange();

		assertThat(response).hasStatus(HttpStatus.NOT_FOUND);
	}

	@Test
	void cityAlreadySelected() {
		doThrow(new CityAlreadySelectedException(42)).when(cityService).addCityById(anyLong());
		var response = mvc.post()
			.uri("/api/city")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{ \"id\": 42 }")
			.exchange();

		assertThat(response).hasStatus(HttpStatus.CONFLICT);
	}

}
