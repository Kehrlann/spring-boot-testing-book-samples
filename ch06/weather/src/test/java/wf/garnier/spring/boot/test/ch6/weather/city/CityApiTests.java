package wf.garnier.spring.boot.test.ch6.weather.city;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch6.weather.city.internal.CityRepository;
import wf.garnier.spring.boot.test.ch6.weather.city.internal.SelectedCityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest
@AutoConfigureMockMvc
class CityApiTests {

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

	private void selectCity(String name) {
		var city = cityRepository.findByNameIgnoreCase(name).get();
		cityService.addCityById(city.getId());
	}

}
