package wf.garnier.spring.boot.test.ch7.weather.city;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch7.weather.city.internal.CityRepository;
import wf.garnier.spring.boot.test.ch7.weather.city.internal.SelectedCityRepository;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static wf.garnier.spring.boot.test.ch7.weather.city.CityApiTests.USER_NAME;

@ApplicationModuleTest
@AutoConfigureMockMvc
@WithMockUser(username = USER_NAME)
class CityApiTests {

	public static final String USER_NAME = "test-user";

	private static final UserId USER = new UserId(USER_NAME);

	@Autowired
	MockMvcTester mvc;

	@Autowired
	CityService cityService;

	@Autowired
	SelectedCityRepository selectedCityRepository;

	@Autowired
	CityRepository cityRepository;

	City paris;

	@BeforeEach
	void clearRepository() {
		selectedCityRepository.deleteAll();
		paris = cityRepository.findByNameIgnoreCase("paris").get();
	}

	@Test
	void selectCity() {
		var response = mvc.post()
			.uri("/api/city")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{ \"id\": %s }".formatted(paris.getId()))
			.exchange();

		assertThat(response).hasStatus(HttpStatus.CREATED).body().isEmpty();
		var cities = cityService.getSelectedCities(USER);

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

		var cities = cityService.getSelectedCities(USER);

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

		assertThat(cityService.getSelectedCities(USER)).hasSize(1);
	}

	@Test
	void unselectCity() {
		selectCity("Paris");

		var response = mvc.delete().uri("/api/city/{id}", paris.getId()).exchange();

		assertThat(response).hasStatus(HttpStatus.NO_CONTENT);
		assertThat(cityService.getSelectedCities(USER)).isEmpty();
	}

	@Test
	void unselectMissingCity() {
		var response = mvc.delete().uri("/api/city/{id}", paris.getId()).exchange();

		assertThat(response).hasStatus(HttpStatus.NO_CONTENT);
		assertThat(cityService.getSelectedCities(USER)).isEmpty();
	}

	@Test
	void selectedCitiesAreNotSharedBetweenUsers() {
		cityService.addCityById(USER, paris.getId());

		assertThat(cityService.getSelectedCities(USER)).hasSize(1);
		assertThat(cityService.getSelectedCities(new UserId("someone-else"))).isEmpty();
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

	@TestConfiguration
	static class DefaultCsrfConfiguration {

		@Bean
		MockMvcBuilderCustomizer defaultCsrf() {
			return builder -> builder.defaultRequest(MockMvcRequestBuilders.get("/").with(csrf()));
		}

	}

	private void selectCity(String name) {
		var city = cityRepository.findByNameIgnoreCase(name).get();
		cityService.addCityById(USER, city.getId());
	}

}
