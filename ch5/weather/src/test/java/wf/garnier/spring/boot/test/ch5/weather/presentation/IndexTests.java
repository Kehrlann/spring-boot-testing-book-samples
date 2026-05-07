package wf.garnier.spring.boot.test.ch5.weather.presentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch5.weather.city.CityService;
import wf.garnier.spring.boot.test.ch5.weather.city.internal.CityRepository;
import wf.garnier.spring.boot.test.ch5.weather.city.internal.SelectedCityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class IndexTests {

	@Autowired
	MockMvcTester mvc;

	@Autowired
	CityService cityService;

	@Autowired
	SelectedCityRepository selectedCityRepository;

	@Autowired
	CityRepository cityRepository;

	@BeforeEach
	void clearRepository() {
		selectedCityRepository.deleteAll();
		selectCity("paris");
	}

	@Test
	void indexPageLoads() {
		var response = mvc.get().uri("/").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("<h1>Weather App</h1>");
	}

	@Test
	void indexPageHasSelectedCity() {
		var response = mvc.get().uri("/").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("Paris (France)");
	}

	private void selectCity(String name) {
		var city = cityRepository.findByNameIgnoreCase(name).get();
		cityService.addCityById(city.getId());
	}

}
