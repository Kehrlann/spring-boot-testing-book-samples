package wf.garnier.spring.boot.test.ch5.weather.weather;

import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch5.weather.city.City;
import wf.garnier.spring.boot.test.ch5.weather.city.CityService;
import wf.garnier.spring.boot.test.ch5.weather.weather.internal.WeatherDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ApplicationModuleTest
@AutoConfigureMockMvc
class WeatherApiTests {

	@Autowired
	MockMvcTester mvc;

	@MockitoBean
	CityService cityService;

	@MockitoBean
	WeatherDataService weatherDataService;

	@Test
	void getWeather() {
		var paris = makeCity("Paris", "France");
		doReturn(List.of(paris)).when(cityService).getSelectedCities();
		configureMockWeather(paris, new WeatherData(20, 0, 0));

		var response = mvc.get().uri("/api/weather").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
				[
				  {
				    "cityName": "Paris",
				    "country": "France",
				    "weather": "Clear sky",
				    "temperature": 20.0,
				    "windSpeed": 0.0
				  }
				]
				""").extractingPath("$.[0].cityId").isEqualTo(paris.getId());
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

	private void configureMockWeather(City paris, WeatherData value) {
		when(weatherDataService.getCurrentWeather(paris.getLatitude(), paris.getLongitude())).thenReturn(value);
	}

	@Test
	void getWeatherMultipleCities() {
		var lagos = makeCity("Lagos", "Nigeria");
		var shenzhen = makeCity("Shenzhen", "China");
		configureMockWeather(lagos, new WeatherData(25, 0, 0));
		configureMockWeather(shenzhen, new WeatherData(17, 5, 1));
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

	private static @NonNull City makeCity(final String name, final String country) {
		// this is a way to "mock" a city without using Mockito
		var id = new Random().nextInt();
		var lat = new Random().nextDouble();
		var lon = new Random().nextDouble();
		return new City() {

			@Override
			public Integer getId() {
				return id;
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public String getCountry() {
				return country;
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
	}

}
