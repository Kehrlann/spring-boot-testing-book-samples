package wf.garnier.spring.boot.test.ch2.weather;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch2.weather.model.City;
import wf.garnier.spring.boot.test.ch2.weather.model.Selection;
import wf.garnier.spring.boot.test.ch2.weather.repository.SelectionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class WeatherApplicationTests {

	@Autowired
	private MockMvcTester mvc;

	@Autowired
	private SelectionRepository selectionRepository;

	@BeforeEach
	void setUp() {
		selectionRepository.deleteAll();
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
	void selectCity() {
		//@formatter:off
		var resp = mvc.post()
				.uri("/city/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cityName\": \"Paris\"}")
				.exchange();
		//@formatter:on

		assertThat(resp).hasStatus(HttpStatus.CREATED);

		assertThat(selectionRepository.findAll()).hasSize(1)
			.first()
			.extracting(Selection::getCity)
			.extracting(City::getName)
			.isEqualTo("Paris");
	}

	@Test
	void selectUnknown() {
		//@formatter:off
		var resp = mvc.post()
				.uri("/city/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cityName\": \"Foobar\"}")
				.exchange();
		//@formatter:on

		assertThat(resp).hasStatus(HttpStatus.BAD_REQUEST);

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

}
