package wf.garnier.spring.boot.test.ch5.weather;

import java.io.IOException;
import java.util.List;

import org.htmlunit.ScriptException;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.javascript.host.event.KeyboardEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch5.weather.openmeteo.WeatherData;
import wf.garnier.spring.boot.test.ch5.weather.openmeteo.WeatherService;
import wf.garnier.spring.boot.test.ch5.weather.selection.City;
import wf.garnier.spring.boot.test.ch5.weather.selection.CityRepository;
import wf.garnier.spring.boot.test.ch5.weather.selection.Selection;
import wf.garnier.spring.boot.test.ch5.weather.selection.SelectionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class HtmlUnitTests {

	@Autowired
	private WebClient webClient;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private SelectionRepository selectionRepository;

	@MockitoBean
	private WeatherService weatherService;

	@BeforeEach
	void setUp() {
		selectionRepository.deleteAll();
		webClient.getOptions().setFetchPolyfillEnabled(true);
		when(weatherService.getCurrentWeather(anyDouble(), anyDouble())).thenReturn(new WeatherData(20, 0, 0));
	}

	@Test
	void mainPage() throws IOException {
		selectCity("Paris");

		HtmlPage page = webClient.getPage("/");

		List<DomNode> cities = page.querySelectorAll(".cities-grid > .card");

		assertThat(cities).hasSize(1)
			.first()
			.extracting(DomNode::getTextContent)
			.asString()
			.contains("Paris (France)")
			.contains("Temperature: 20.0°C")
			.contains("Wind Speed: 0.0 km/h")
			.contains("Weather: Clear sky");
	}

	@Test
	void mainPageNoCities() throws IOException {
		HtmlPage page = webClient.getPage("/");

		var cities = page.querySelectorAll(".cities-grid > .card");

		assertThat(cities).isEmpty();
	}

	@Test
	void mainPageMultipleCities() throws IOException {
		selectCity("Paris");
		selectCity("Delhi");

		HtmlPage page = webClient.getPage("/");

		var cities = page.querySelectorAll(".cities-grid .card-title").stream().map(DomNode::getTextContent);

		assertThat(cities).containsExactly("Delhi (India)", "Paris (France)");
	}

	@Test
	void deleteCity() throws IOException {
		selectCity("Paris");

		HtmlPage page = webClient.getPage("/");

		page.<HtmlButton>querySelector("form[data-role=\"delete-city\"] > button").click();
		webClient.waitForBackgroundJavaScript(1000);

		var cities = page.querySelectorAll(".cities-grid .card");

		assertThat(cities).isEmpty();
	}

	@Test
	void autocomplete() throws IOException {
		HtmlPage page = webClient.getPage("/");

		var citySearchInput = page.<HtmlInput>querySelector("input#citySearch");
		citySearchInput.type("jak");
		webClient.waitForBackgroundJavaScript(1000); // wait for autocomplete results

		var autocompleteResults = page.querySelector("#cityResults")
			.getChildNodes()
			.stream()
			.map(DomNode::getTextContent)
			.map(String::trim);

		assertThat(autocompleteResults).containsExactlyInAnyOrder("Djakotomé (Benin)", "Jakarta (Indonesia)",
				"Kamirenjaku (Japan)");
	}

	@Test
	void addCityWithKeyboard() throws IOException {
		HtmlPage page = webClient.getPage("/");

		var citySearchInput = page.<HtmlInput>querySelector("input#citySearch");
		citySearchInput.type("Paris");
		webClient.waitForBackgroundJavaScript(1000); // wait for autocomplete results

		citySearchInput.type(KeyboardEvent.DOM_VK_RETURN); // "Enter"

		webClient.waitForBackgroundJavaScript(1000); // wait for new cities data

		var cities = page.querySelectorAll(".cities-grid .card");

		assertThat(cities).hasSize(1).first().extracting(DomNode::getTextContent).asString().contains("Paris (France)");
	}

	@Test
	void addCityWithMouse() throws IOException {
		HtmlPage page = webClient.getPage("/");

		HtmlInput citySearchInput = page.querySelector("input#citySearch");
		citySearchInput.type("bogot");
		webClient.waitForBackgroundJavaScript(1000);

		page.<HtmlElement>querySelector(".autocomplete-item").click();
		webClient.waitForBackgroundJavaScript(1000);

		var cities = page.querySelectorAll(".cities-grid .card");

		assertThat(cities).hasSize(1)
			.first()
			.extracting(DomNode::getTextContent)
			.asString()
			.contains("Bogotá (Colombia)");
	}

	@Test
	void addCityWithKeyboardMultipleChoices() throws IOException {
		HtmlPage page = webClient.getPage("/");

		var citySearchInput = page.<HtmlInput>querySelector("input#citySearch");
		citySearchInput.type("Ank");
		webClient.waitForBackgroundJavaScript(1000); // wait for autocomplete results

		citySearchInput.type(KeyboardEvent.DOM_VK_DOWN); // "Down arrow"
		citySearchInput.type(KeyboardEvent.DOM_VK_DOWN); // "Down arrow"
		citySearchInput.type(KeyboardEvent.DOM_VK_RETURN); // "Enter"

		webClient.waitForBackgroundJavaScript(1000); // wait for weather data

		var cities = page.querySelectorAll(".cities-grid .card");

		assertThat(cities).hasSize(1)
			.first()
			.extracting(DomNode::getTextContent)
			.asString()
			.contains("Ankara (Turkey)");
	}

	private City selectCity(String name) {
		var city = cityRepository.findByNameIgnoreCase(name).get();
		selectionRepository.save(new Selection(city));
		return city;
	}

}
