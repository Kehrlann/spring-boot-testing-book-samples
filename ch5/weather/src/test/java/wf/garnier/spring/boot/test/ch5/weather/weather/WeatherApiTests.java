// tag::package[]
package wf.garnier.spring.boot.test.ch5.weather.weather; <1>

// end::package[]

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch5.weather.city.City;
import wf.garnier.spring.boot.test.ch5.weather.city.CityService;
import wf.garnier.spring.boot.test.ch5.weather.weather.internal.WeatherDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;
import static org.mockito.Mockito.doReturn;

// tag::class[]
@ApplicationModuleTest <1>
@AutoConfigureMockMvc
class WeatherApiTests {

	// end::class[]
	// tag::dependencies[]
	@Autowired
	MockMvcTester mvc;

	@MockitoBean
	CityService cityService; <2>

	@TestBean
	WeatherDataService weatherDataService; <3>

	// end::dependencies[]
	// tag::test[]
	@Test
	void getWeather() {
		var paris = new TestCity("Paris", "France"); <4>
		doReturn(List.of(paris)).when(cityService).getSelectedCities(); <4>

		var response = mvc.get().uri("/api/weather").exchange();

		assertThat(response)
				.hasStatus(HttpStatus.OK)
				.bodyJson().isLenientlyEqualTo("""
				[
				  {
				    "cityName": "Paris",
				    "country": "France",
				    "weather": "Clear sky",
				    "temperature": 20.0,
				    "windSpeed": 0.0
				  }
				]
				""")
				.extractingPath("$.[0].cityId").isEqualTo(paris.getId());
	}
	// end::test[]

	/**
	 * Alternative assertions for testing getWeather.
	 */
	@Test
	void getWeatherAlternate() {
		var paris = new TestCity("Paris", "France");
		doReturn(List.of(paris)).when(cityService).getSelectedCities();

		var response = mvc.get().uri("/api/weather").exchange();

		assertThat(response).bodyJson()
			.convertTo(InstanceOfAssertFactories.list(WeatherResponse.class))
			.hasSize(1)
			.first()
			.satisfies(wr -> {
				assertThat(wr.cityName()).isEqualTo("Paris");
				assertThat(wr.temperature()).isCloseTo(20, withPercentage(10));
			});
		assertThat(response).bodyJson().extractingPath("$.[0]").convertTo(WeatherResponse.class).satisfies(wr -> {
			assertThat(wr.cityName()).isEqualTo("Paris");
			assertThat(wr.temperature()).isCloseTo(20, withPercentage(10));
		});
	}

	@Test
	void getWeatherMultipleCities() {
		var lagos = new TestCity("Lagos", "Nigeria");
		var shenzhen = new TestCity("Shenzhen", "China");
		((TestWeatherDataService) weatherDataService).setWeatherFor(lagos, new WeatherData(25, 0, 0));
		((TestWeatherDataService) weatherDataService).setWeatherFor(shenzhen, new WeatherData(17, 5, 1));
		doReturn(List.of(lagos, shenzhen)).when(cityService).getSelectedCities();

		var response = mvc.get().uri("/api/weather").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
				[
				  {
				    "cityName": "Lagos",
				    "country": "Nigeria",
				    "cityId": %s,
				    "weather": "Clear sky",
				    "temperature": 25.0,
				    "windSpeed": 0.0
				  },
				  {
				    "cityName": "Shenzhen",
				    "country": "China",
				    "cityId": %s,
				    "weather": "Partly cloudy",
				    "temperature": 17.0,
				    "windSpeed": 5.0
				  }
				]
				""".formatted(lagos.getId(), shenzhen.getId()));
	}

	@Test
	void getWeatherNoCity() {
		var response = mvc.get().uri("/api/weather").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().isEqualTo("[]");
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record WeatherResponse(String cityName, String country, Double temperature) {
	}

	/**
	 * Test-focused implementation of {@link City}.
	 */
	static class TestCity implements City {

		private final int id = new Random().nextInt();

		private final double lat = new Random().nextDouble();

		private final double lon = new Random().nextDouble();

		private final String name;

		private final String country;

		public TestCity(String name, String country) {
			this.name = name;
			this.country = country;
		}

		@Override
		public Integer getId() {
			return id;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public String getCountry() {
			return this.country;
		}

		@Override
		public double getLatitude() {
			return lat;
		}

		@Override
		public double getLongitude() {
			return lon;
		}

	};

	/**
	 * This is a hand-rolled stub, with a default value of temperature = 20, wind = 0 and
	 * weather = clear.
	 * <p>
	 * It is configurable through {@code setWeatherFor}.
	 */
	static TestWeatherDataService weatherDataService() {
		return new TestWeatherDataService();
	}

	private static class TestWeatherDataService implements WeatherDataService {

		private final Map<City, WeatherData> preRecordedWeather = new HashMap<>();

		@Override
		public WeatherData getCurrentWeather(double latitude, double longitude) {
			return preRecordedWeather.entrySet().stream()
					.filter(e -> {
						var city = e.getKey();
						return city.getLatitude() == latitude && city.getLongitude() == longitude;
					})
					.findFirst()
					.map(Map.Entry::getValue)
					.orElse(new WeatherData(20, 0, 0));
		}

		void setWeatherFor(City city, WeatherData weatherData) {
			preRecordedWeather.put(city, weatherData);
		}

	}
	// tag::class[]

}
// end::class[]
