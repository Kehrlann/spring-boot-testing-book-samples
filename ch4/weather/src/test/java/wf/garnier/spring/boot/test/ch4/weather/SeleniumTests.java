package wf.garnier.spring.boot.test.ch4.weather;

import java.io.IOException;
import java.time.Duration;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import wf.garnier.spring.boot.test.ch4.weather.city.City;
import wf.garnier.spring.boot.test.ch4.weather.city.CityRepository;
import wf.garnier.spring.boot.test.ch4.weather.openmeteo.WeatherData;
import wf.garnier.spring.boot.test.ch4.weather.openmeteo.WeatherService;
import wf.garnier.spring.boot.test.ch4.weather.selection.Selection;
import wf.garnier.spring.boot.test.ch4.weather.selection.SelectionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SeleniumTests {

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private SelectionRepository selectionRepository;

	@MockitoBean
	private WeatherService weatherService;

	@LocalServerPort
	private int port;

	private static ChromeDriverService driverService;

	private RemoteWebDriver driver;

	@BeforeAll
	static void startChromeDriverService() throws Exception {
		driverService = new ChromeDriverService.Builder().usingAnyFreePort().build();
		driverService.start();
	}

	@AfterAll
	static void stopChromeDriverService() {
		driverService.stop();
	}

	@BeforeEach
	void setUp() {
		selectionRepository.deleteAll();
		when(weatherService.getCurrentWeather(anyDouble(), anyDouble())).thenReturn(new WeatherData(20, 0, 0));
	}

	@BeforeEach
	void setupChromeDriver() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new");
		this.driver = new RemoteWebDriver(driverService.getUrl(), options);
		// Enable dev tools
		// this.driver = (RemoteWebDriver) new Augmenter().augment(baseDriver); // TODO ??
		this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
	}

	@Test
	void mainPage() throws IOException {
		selectCity("Paris");
		this.driver.get("http://localhost:" + port);

		var cities = this.driver.findElements(By.cssSelector(".cities-grid > .card"));

		assertThat(cities).hasSize(1)
			.first()
			.extracting(c -> c.findElement(By.cssSelector(".full-display")).getText())
			.asString()
			.contains("Paris (France)")
			.contains("Temperature: 20.0°C")
			.contains("Wind Speed: 0.0 km/h")
			.contains("Weather: Clear sky");
	}

	@Test
	void mainPageNoCities() throws IOException {
		// HtmlPage page = webClient.getPage("/");
		//
		// var cities = page.querySelectorAll(".cities-grid > .card");
		//
		// assertThat(cities).isEmpty();
	}

	@Test
	void mainPageMultipleCities() throws IOException {
		// selectCity("Paris");
		// selectCity("Delhi");
		//
		// HtmlPage page = webClient.getPage("/");
		//
		// var cities = page.querySelectorAll(".cities-grid
		// .card-title").stream().map(DomNode::getTextContent);
		//
		// assertThat(cities).containsExactly("Delhi (India)", "Paris (France)");
	}

	@Nested
	class DisplayModeTests {

		enum DisplayMode {

			FULL, COMPACT

		}

		@Test
		void initialDefaultFullDisplay() throws IOException {
			// selectCity("Paris");
			//
			// HtmlPage page = webClient.getPage("/");
			//
			// assertDisplay(page, DisplayMode.FULL);
		}

		@Test
		void initialFullDisplay() throws IOException {
			// selectCity("Paris");
			//
			// HtmlPage page = webClient.getPage("/?display=full");
			//
			// assertDisplay(page, DisplayMode.FULL);
		}

		@Test
		void initialCompactDisplay() throws IOException {
			// selectCity("Paris");
			//
			// HtmlPage page = webClient.getPage("/?display=compact");
			//
			// assertDisplay(page, DisplayMode.COMPACT);
		}

		@Test
		void toggleDisplay() throws IOException {
			// selectCity("Paris");
			//
			// HtmlPage page = webClient.getPage("/");
			//
			// page.<HtmlButton>querySelector("#button-display-compact").click();
			// assertDisplay(page, DisplayMode.COMPACT);
			// assertThat(page.getUrl().getQuery()).isEqualTo("display=compact");
			// page.<HtmlButton>querySelector("#button-display-full").click();
			// assertDisplay(page, DisplayMode.FULL);
			// assertThat(page.getUrl().getQuery()).isEqualTo("display=full");
		}

		static void assertDisplay(HtmlPage page, DisplayMode mode) {
			var compactDisplay = page.querySelector(".cities-grid > .card > .compact-display");
			var fullDisplay = page.querySelector(".cities-grid > .card > .full-display");

			assertThat(compactDisplay.isDisplayed()).isEqualTo(mode == DisplayMode.COMPACT);
			assertThat(fullDisplay.isDisplayed()).isEqualTo(mode == DisplayMode.FULL);
		}

	}

	@Test
	void deleteCity() throws IOException {
		// selectCity("Paris");
		//
		// HtmlPage page = webClient.getPage("/");
		//
		// page.<HtmlButton>querySelector("form[data-role=\"delete-city\"] >
		// button").click();
		// webClient.waitForBackgroundJavaScript(1000);
		//
		// var cities = page.querySelectorAll(".cities-grid .card");
		//
		// assertThat(cities).isEmpty();
	}

	@Test
	void autocomplete() throws IOException {
		// HtmlPage page = webClient.getPage("/");
		//
		// var citySearchInput = page.<HtmlInput>querySelector("input#citySearch");
		// citySearchInput.type("jak");
		// webClient.waitForBackgroundJavaScript(1000); // wait for autocomplete results
		//
		// var autocompleteResults = page.querySelector("#cityResults")
		// .getChildNodes()
		// .stream()
		// .map(DomNode::getTextContent)
		// .map(String::trim);
		//
		// assertThat(autocompleteResults).containsExactlyInAnyOrder("Djakotomé (Benin)",
		// "Jakarta (Indonesia)",
		// "Kamirenjaku (Japan)");
	}

	@Test
	void addCityWithKeyboard() throws IOException {
		// HtmlPage page = webClient.getPage("/");
		//
		// var citySearchInput = page.<HtmlInput>querySelector("input#citySearch");
		// citySearchInput.type("Paris");
		// webClient.waitForBackgroundJavaScript(1000); // wait for autocomplete results
		//
		// citySearchInput.type(KeyboardEvent.DOM_VK_RETURN); // "Enter"
		//
		// webClient.waitForBackgroundJavaScript(1000); // wait for new cities data
		//
		// var cities = page.querySelectorAll(".cities-grid .card");
		//
		// assertThat(cities).hasSize(1).first().extracting(DomNode::getTextContent).asString().contains("Paris
		// (France)");
	}

	@Test
	void addCityWithMouse() throws IOException {
		// HtmlPage page = webClient.getPage("/");
		//
		// var citySearchInput = page.<HtmlInput>querySelector("input#citySearch");
		// citySearchInput.type("Paris");
		// webClient.waitForBackgroundJavaScript(1000); // wait for autocomplete results
		//
		// page.<HtmlElement>querySelector(".autocomplete-item").click();
		//
		// webClient.waitForBackgroundJavaScript(1000); // wait for new cities data
		//
		// var cities = page.querySelectorAll(".cities-grid .card");
		//
		// assertThat(cities).hasSize(1).first().extracting(DomNode::getTextContent).asString().contains("Paris
		// (France)");
	}

	@Test
	void addCityWithKeyboardMultipleChoices() throws IOException {
		// HtmlPage page = webClient.getPage("/");
		//
		// var citySearchInput = page.<HtmlInput>querySelector("input#citySearch");
		// citySearchInput.type("Ank");
		// webClient.waitForBackgroundJavaScript(1000); // wait for autocomplete results
		//
		// citySearchInput.type(KeyboardEvent.DOM_VK_DOWN); // "Down arrow"
		// citySearchInput.type(KeyboardEvent.DOM_VK_DOWN); // "Down arrow"
		// citySearchInput.type(KeyboardEvent.DOM_VK_RETURN); // "Enter"
		//
		// webClient.waitForBackgroundJavaScript(1000); // wait for new cities data
		//
		// var cities = page.querySelectorAll(".cities-grid .card");
		//
		// assertThat(cities).hasSize(1)
		// .first()
		// .extracting(DomNode::getTextContent)
		// .asString()
		// .contains("Ankara (Turkey)");
	}

	@Test
	void cannotLoadModernnJavascript() {
		// assertThatThrownBy(() ->
		// webClient.getPage("/?mode=modern")).isInstanceOf(ScriptException.class)
		// .message()
		// .contains("missing ; before statement");
	}

	private City selectCity(String name) {
		var city = cityRepository.findByNameIgnoreCase(name).get();
		selectionRepository.save(new Selection(city));
		return city;
	}

}
