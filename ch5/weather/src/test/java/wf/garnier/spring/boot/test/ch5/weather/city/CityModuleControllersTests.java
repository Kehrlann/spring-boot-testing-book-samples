package wf.garnier.spring.boot.test.ch5.weather.city;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.modulith.test.ModuleSlicing;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Similar test to {@link CityControllersTests}, but we do not need to specify the
 * controllers in {@link WebMvcTest}: only controllers in the City module
 * ({@code package wf.garnier.spring.boot.test.ch5.weather.city}) will be included.
 */
// tag::class[]
@ModuleSlicing
@WebMvcTest
class CityModuleControllersTests {

	// end::class[]
	// tag::fields[]
	@MockitoBean
	CityService cityService;

	@Autowired
	MockMvcTester mvc;

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
	// tag::class[]

}
// end::class[]