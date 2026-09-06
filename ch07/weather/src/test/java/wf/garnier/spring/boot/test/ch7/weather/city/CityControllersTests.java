package wf.garnier.spring.boot.test.ch7.weather.city;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.modulith.test.ModuleSlicing;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@ModuleSlicing
@WebMvcTest
@WithMockUser
class CityControllersTests {

	@MockitoBean
	CityService cityService;

	@Autowired
	MockMvcTester mvc;

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

	@TestConfiguration
	static class DefaultCsrfConfiguration {

		@Bean
		MockMvcBuilderCustomizer defaultCsrf() {
			return builder -> builder.defaultRequest(MockMvcRequestBuilders.get("/").with(csrf()));
		}

	}

}