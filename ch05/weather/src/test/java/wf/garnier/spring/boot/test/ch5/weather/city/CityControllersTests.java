package wf.garnier.spring.boot.test.ch5.weather.city;

import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch5.weather.city.internal.CityController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

// tag::class[]
@WebMvcTest(controllers = { CityController.class }) // <1>
class CityControllersTests {

	// end::class[]
	// tag::fields[]
	@MockitoBean
	CityService cityService; // <2>

	@Autowired
	MockMvcTester mvc; // <3>

	// end::fields[]
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

	// tag::test[]
	@Test
	void cityAlreadySelected() {
		doThrow(new CityAlreadySelectedException(42)) // <4>
			.when(cityService) // <4>
			.addCityById(anyLong()); // <4>

		var response = mvc.post()
			.uri("/api/city")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{ \"id\": 42 }")
			.exchange();

		assertThat(response).hasStatus(HttpStatus.CONFLICT);
	}
	// end::test[]

	// tag::class[]

}
// end::class[]
