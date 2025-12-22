package wf.garnier.spring.boot.test.ch4.weather;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.support.ui.FluentWait;
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
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

// tag::class[]
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SeleniumTests {

	// end::class[]
	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private SelectionRepository selectionRepository;

	@MockitoBean
	private WeatherService weatherService;

	// tag::setup-webdriver[]
	//@formatter:off
	@LocalServerPort int port;
	static DriverService driverService;
	static RemoteWebDriver driver;
	//@formatter:on

	@BeforeAll
	static void startChromeDriverService() throws Exception {
		driverService = ChromeDriverService.createDefaultService(); // <1>
		driverService.start(); // <1>

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new");
		driver = new RemoteWebDriver(driverService.getUrl(), options); // <2>
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1)); // <3>
	}

	@AfterAll
	static void stopChromeDriverService() {
		driverService.stop(); // <4>
	}
	// end::setup-webdriver[]

	@BeforeEach
	void setUp() {
		selectionRepository.deleteAll();
		when(weatherService.getCurrentWeather(anyDouble(), anyDouble())).thenReturn(new WeatherData(20, 0, 0));
	}

	@Test
	void mainPage() {
		selectCity("Paris");
		driver.get("http://localhost:" + port + "/?mode=modern");

		var cities = driver.findElements(By.cssSelector(".cities-grid > .card > .full-display"));

		assertThat(cities).hasSize(1)
			.first()
			.extracting(WebElement::getText)
			.asString()
			.contains("Paris (France)")
			.contains("Temperature: 20.0°C")
			.contains("Wind Speed: 0.0 km/h")
			.contains("Weather: Clear sky");
	}

	@Test
	void mainPageNoCities() {
		driver.get("http://localhost:" + port + "/?mode=modern");

		var cities = driver.findElements(By.cssSelector(".cities-grid > .card"));

		assertThat(cities).isEmpty();
	}

	@Test
	void mainPageMultipleCities() {
		selectCity("Paris");
		selectCity("Delhi");

		driver.get("http://localhost:" + port + "/?mode=modern");

		var cities = driver.findElements(By.cssSelector(".cities-grid > .card > .full-display > .card-title"))
			.stream()
			.map(WebElement::getText)
			.toList();

		assertThat(cities).containsExactly("Delhi (India)", "Paris (France)");
	}

	@Nested
	class DisplayModeTests {

		enum DisplayMode {

			FULL, COMPACT

		}

		@Test
		void initialDefaultFullDisplay() {
			selectCity("Paris");

			driver.get("http://localhost:" + port + "/?mode=modern");

			assertDisplay(DisplayMode.FULL);
		}

		@Test
		void initialFullDisplay() {
			selectCity("Paris");

			driver.get("http://localhost:" + port + "/?mode=modern&display=full");

			assertDisplay(DisplayMode.FULL);
		}

		@Test
		void initialCompactDisplay() {
			selectCity("Paris");

			driver.get("http://localhost:" + port + "/?mode=modern&display=compact");

			assertDisplay(DisplayMode.COMPACT);
		}

		@Test
		void toggleDisplay() {
			selectCity("Paris");

			driver.get("http://localhost:" + port + "/?mode=modern");

			driver.findElement(By.id("button-display-compact")).click();
			assertDisplay(DisplayMode.COMPACT);
			assertThat(driver.getCurrentUrl()).contains("display=compact");

			driver.findElement(By.id("button-display-full")).click();
			assertDisplay(DisplayMode.FULL);
			assertThat(driver.getCurrentUrl()).contains("display=full");
		}

		void assertDisplay(DisplayMode mode) {
			var compactDisplay = driver.findElement(By.cssSelector(".cities-grid > .card > .compact-display"));
			var fullDisplay = driver.findElement(By.cssSelector(".cities-grid > .card > .full-display"));

			assertThat(compactDisplay.isDisplayed()).isEqualTo(mode == DisplayMode.COMPACT);
			assertThat(fullDisplay.isDisplayed()).isEqualTo(mode == DisplayMode.FULL);
		}

	}

	@Test
	void deleteCity() {
		// Here, we assess the _absence_ of elements, so we want to turn off implicit
		// waits to speed up the test. Otherwise, when we try to locate an element that's
		// missing, we'll wait for the "implicit wait" duration.
		driver.manage().timeouts().implicitlyWait(Duration.ZERO);
		selectCity("Paris");

		driver.get("http://localhost:" + port + "/?mode=modern");

		driver.findElement(By.cssSelector("form[data-role=\"delete-city\"] > button")).click();
		new FluentWait<>(driver).withTimeout(Duration.ofSeconds(1))
			.pollingEvery(Duration.ofMillis(100))
			.until(driver -> driver.findElements(By.cssSelector(".cities-grid > .card")).isEmpty());

		assertThat(driver.findElements(By.cssSelector(".cities-grid .card"))).isEmpty();
	}

	@Test
	void autocomplete() {
		driver.get("http://localhost:" + port + "/?mode=modern");

		var citySearchInput = driver.findElement(By.id("citySearch"));
		citySearchInput.sendKeys("jak");

		var autocompleteResults = driver.findElement(By.id("cityResults"))
			.findElements(By.cssSelector(".autocomplete-item"))
			.stream()
			.map(element -> element.getText().trim());

		assertThat(autocompleteResults).contains("Djakotomé (Benin)", "Jakarta (Indonesia)", "Kamirenjaku (Japan)");
	}

	@Test
	void addCityWithKeyboard() {
		driver.get("http://localhost:" + port + "/?mode=modern");

		WebElement citySearchInput = driver.findElement(By.id("citySearch"));
		citySearchInput.sendKeys("Paris");

		var autocomplete = driver.findElements(By.className("autocomplete-item"));
		assertThat(autocomplete).hasSize(1).first().extracting(WebElement::getText).isEqualTo("Paris (France)");
		citySearchInput.sendKeys(Keys.RETURN);

		var cities = driver.findElements(By.cssSelector(".cities-grid > .card"));

		assertThat(cities).hasSize(1).first().extracting(WebElement::getText).asString().contains("Paris (France)");
	}

	@Test
	void addCityWithMouse() {
		driver.get("http://localhost:" + port + "/?mode=modern");

		var citySearchInput = driver.findElement(By.id("citySearch"));
		citySearchInput.sendKeys("Paris");

		driver.findElement(By.cssSelector(".autocomplete-item")).click();

		var cities = driver.findElements(By.cssSelector(".cities-grid .card"));
		assertThat(cities).hasSize(1).first().extracting(WebElement::getText).asString().contains("Paris (France)");
	}

	@Test
	void addCityWithKeyboardMultipleChoices() {
		driver.get("http://localhost:" + port + "/?mode=modern");

		var citySearchInput = driver.findElement(By.id("citySearch"));
		citySearchInput.sendKeys("Ank");
		// Ensure the auto-complete is shown
		driver.findElement(By.cssSelector(".autocomplete-item"));

		citySearchInput.sendKeys(org.openqa.selenium.Keys.ARROW_DOWN);
		citySearchInput.sendKeys(org.openqa.selenium.Keys.ARROW_DOWN);
		citySearchInput.sendKeys(org.openqa.selenium.Keys.RETURN);

		var cities = driver.findElements(By.cssSelector(".cities-grid .card"));

		assertThat(cities).hasSize(1).first().extracting(WebElement::getText).asString().contains("Ankara (Turkey)");
	}

	private City selectCity(String name) {
		var city = cityRepository.findByNameIgnoreCase(name).get();
		selectionRepository.save(new Selection(city));
		return city;
	}

	// tag::class[]

}
// end::class[]
