package wf.garnier.spring.boot.test.ch2.weather;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch2.weather.city.City;
import wf.garnier.spring.boot.test.ch2.weather.openmeteo.WeatherService;
import wf.garnier.spring.boot.test.ch2.weather.selection.CityWeather;
import wf.garnier.spring.boot.test.ch2.weather.selection.Selection;
import wf.garnier.spring.boot.test.ch2.weather.openmeteo.WeatherData;
import wf.garnier.spring.boot.test.ch2.weather.selection.SelectionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class WeatherApplicationTests {

	@Autowired
	private MockMvcTester mvc;

	@Autowired
	private SelectionRepository selectionRepository;

	@MockitoBean
	private WeatherService weatherService;

	@BeforeEach
	void setUp() {
		selectionRepository.deleteAll();
		when(weatherService.getCurrentWeather(anyDouble(), anyDouble())).thenReturn(new WeatherData(20, 0, 0));
	}

	@Test
	void contextLoads() {

	}

	@Test
	void indexPageLoads() {
		//@formatter:off
		var response = mvc.get()
				.uri("/")
				.exchange();
		assertThat(response)
				.hasStatus(HttpStatus.OK)
				.bodyText()
				.contains("<h1>Weather App</h1>");
		//@formatter:on
	}

	@Test
	void indexPageWithCity() {
		//@formatter:off
		mvc.post()
				.uri("/city/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cityName\": \"Paris\"}")
				.exchange();
		var response = mvc.get()
				.uri("/")
				.exchange();
		assertThat(response)
				.hasStatus(HttpStatus.OK)
				.bodyText()
				.contains("Paris");
		//@formatter:on
	}

	@Test
	void selectCity() {
		//@formatter:off
		var response = mvc.post()
				.uri("/city/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cityName\": \"Paris\"}")
				.exchange();
		//@formatter:on

		assertThat(response).hasStatus(HttpStatus.CREATED);

		assertThat(selectionRepository.findAll()).hasSize(1)
			.first()
			.extracting(Selection::getCity)
			.extracting(City::getName)
			.isEqualTo("Paris");
	}

	@Test
	void selectUnknown() {
		//@formatter:off
		var response = mvc.post()
				.uri("/city/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cityName\": \"Foobar\"}")
				.exchange();
		//@formatter:on

		assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);

		assertThat(selectionRepository.findAll()).hasSize(0);
	}

	@Test
	void selectTwice() {
		//@formatter:off
		mvc.post()
				.uri("/city/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cityName\": \"Bogotá\"}")
				.exchange();
		mvc.post()
				.uri("/city/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cityName\": \"Bogotá\"}")
				.exchange();
		//@formatter:on

		assertThat(selectionRepository.findAll()).hasSize(1);
	}

	@Test
	void getWeather() {
        when(weatherService.getCurrentWeather(anyDouble(), anyDouble())).thenReturn(new WeatherData(22.6, 0, 1));

		//@formatter:off
		mvc.post()
			.uri("/city/add")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"cityName\": \"Paris\"}")
			.exchange();
		mvc.post()
			.uri("/city/add")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"cityName\": \"Beijing\"}")
			.exchange();
		//@formatter:on

		var response = mvc.get().uri("/weather").accept(MediaType.APPLICATION_JSON).exchange();
		assertThat(response).hasStatus(HttpStatus.OK);

		assertThat(response).bodyJson()
			.extractingPath("$.[*].cityName")
			.asArray()
			.hasSize(2)
			.containsExactly("Beijing", "Paris");

		assertThat(response).bodyJson()
				.convertTo(list(CityWeather.class))
				.hasSize(2)
				.last()
				.hasFieldOrPropertyWithValue("cityName", "Paris")
				.hasFieldOrPropertyWithValue("temperature", 22.6);
	}

	// TODO: delete

}
