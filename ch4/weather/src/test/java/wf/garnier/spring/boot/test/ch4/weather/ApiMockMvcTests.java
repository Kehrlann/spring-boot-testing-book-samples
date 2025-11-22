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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiMockMvcTests {

	@Autowired
	private MockMvc mvc;

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
	void indexPageLoads() throws Exception {
		//@formatter:off
		mvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("<h1>Weather App</h1>")));
		//@formatter:on
	}

	@Test
	void indexPageHasSelectedCity() throws Exception {
		selectCity("Paris");

		//@formatter:off
	     mvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Paris (France)")));
	     //@formatter:on
	}

	@Test
	void selectCity() throws Exception {
		//@formatter:off
		mvc.perform(post("/api/city")
            .contentType(MediaType.APPLICATION_JSON)
			.content("{ \"id\": %s }".formatted(paris.getId())))
            .andExpect(status().isCreated());
		//@formatter:on

		var cities = selectionRepository.findAll();
		assertThat(cities).hasSize(1);
		assertThat(cities.get(0).getCity().getName()).isEqualTo("Paris");
	}

	@Test
	void selectCityTwice() throws Exception {
		//@formatter:off
		mvc.perform(post("/api/city")
            .contentType(MediaType.APPLICATION_JSON)
			.content("{ \"id\": %s }".formatted(paris.getId())))
            .andExpect(status().isCreated());
		mvc.perform(post("/api/city")
            .contentType(MediaType.APPLICATION_JSON)
			.content("{ \"id\": %s }".formatted(paris.getId())))
            .andExpect(status().isConflict());
		//@formatter:on

		assertThat(selectionRepository.count()).isEqualTo(1);
	}

	@Test
	void unselectCity() throws Exception {
		selectCity("Paris");

		mvc.perform(delete("/api/city/{id}", paris.getId())).andExpect(status().isNoContent());

		assertThat(selectionRepository.count()).isEqualTo(0);
	}

	@Test
	void unselectMissingCity() throws Exception {
		mvc.perform(delete("/api/city/{id}", paris.getId())).andExpect(status().isNoContent());

		assertThat(selectionRepository.count()).isEqualTo(0);
	}

	@Test
	void getWeather() throws Exception {
		selectCity("Paris");

		mvc.perform(get("/api/weather"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].cityName").value("Paris"))
			.andExpect(jsonPath("$[0].country").value("France"))
			.andExpect(jsonPath("$[0].cityId").value(paris.getId()))
			.andExpect(jsonPath("$[0].weather").value("Clear sky"))
			.andExpect(jsonPath("$[0].temperature").value(20.0))
			.andExpect(jsonPath("$[0].windSpeed").value(0.0));
	}

	@Test
	void getWeatherMultipleCities() throws Exception {
		var lagos = selectCity("Lagos");
		var shenzhen = selectCity("Shenzhen");
		when(weatherService.getCurrentWeather(anyDouble(), anyDouble())).thenReturn(new WeatherData(25, 0, 0))
			.thenReturn(new WeatherData(17, 5, 1));

		mvc.perform(get("/api/weather"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].cityName").value("Lagos"))
			.andExpect(jsonPath("$[0].country").value("Nigeria"))
			.andExpect(jsonPath("$[0].cityId").value(lagos.getId()))
			.andExpect(jsonPath("$[0].weather").value("Clear sky"))
			.andExpect(jsonPath("$[0].temperature").value(25.0))
			.andExpect(jsonPath("$[0].windSpeed").value(0.0))
			.andExpect(jsonPath("$[1].cityName").value("Shenzhen"))
			.andExpect(jsonPath("$[1].country").value("China"))
			.andExpect(jsonPath("$[1].cityId").value(shenzhen.getId()))
			.andExpect(jsonPath("$[1].weather").value("Partly cloudy"))
			.andExpect(jsonPath("$[1].temperature").value(17.0))
			.andExpect(jsonPath("$[1].windSpeed").value(5.0));
	}

	@Test
	void getWeatherNoCity() throws Exception {
		mvc.perform(get("/api/weather"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$").isEmpty());
	}

	@Test
	void cityAutocomplete() throws Exception {
		mvc.perform(get("/api/city").queryParam("q", "quito"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[*].name").value(containsInAnyOrder("Quito", "Iquitos")));
	}

	@Test
	void cityAutocompleteEmpty() throws Exception {
		mvc.perform(get("/api/city").queryParam("q", ""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(4999));
	}

	@Test
	void cityAutocompleteNoMatch() throws Exception {
		mvc.perform(get("/api/city").queryParam("q", "r'lyeh"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(0));
	}

	private City selectCity(String name) {
		var city = this.cityRepository.findByNameIgnoreCase(name).get();
		selectionRepository.save(new Selection(city));
		return city;
	}

}