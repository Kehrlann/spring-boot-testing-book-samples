package wf.garnier.spring.boot.test.ch2.weather.weather.controller;

import java.util.List;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.City;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.PreferredCity;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.WeatherResponse;
import wf.garnier.spring.boot.test.ch2.weather.weather.repository.PreferredCityRepository;
import wf.garnier.spring.boot.test.ch2.weather.weather.service.WeatherService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PreferredCityRepository preferredCityRepository;

	@MockBean
	private WeatherService weatherService;

	@Test
	void shouldShowWeatherForPreferredCities() throws Exception {
		// Given
		City paris = new City("Paris", "France", 48.8566, 2.3522);
		PreferredCity preferredParis = new PreferredCity(paris);
		WeatherResponse parisWeather = new WeatherResponse(20.0, 5.0, 0); // 0 = Clear sky

		when(preferredCityRepository.findAll()).thenReturn(List.of(preferredParis));
		when(weatherService.getWeather(anyDouble(), anyDouble())).thenReturn(parisWeather);

		// When/Then
		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("cities"))
			.andExpect(view().name("index"))
			.andExpect(model().attribute("cities", List.of(new WeatherController.CityWeather(paris, parisWeather))));
	}

	@Test
	void shouldHandleEmptyPreferredCities() throws Exception {
		// Given
		when(preferredCityRepository.findAll()).thenReturn(List.of());

		// When/Then
		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("cities", List.of()))
			.andExpect(view().name("index"));
	}

	@Test
	void shouldFetchWeatherForEachPreferredCity() throws Exception {
		// Given
		City paris = new City("Paris", "France", 48.8566, 2.3522);
		City london = new City("London", "UK", 51.5074, -0.1278);
		PreferredCity preferredParis = new PreferredCity(paris);
		PreferredCity preferredLondon = new PreferredCity(london);

		WeatherResponse parisWeather = new WeatherResponse(20.0, 5.0, 0); // Clear sky
		WeatherResponse londonWeather = new WeatherResponse(15.0, 8.0, 61); // Rain

		when(preferredCityRepository.findAll()).thenReturn(List.of(preferredParis, preferredLondon));
		when(weatherService.getWeather(48.8566, 2.3522)).thenReturn(parisWeather);
		when(weatherService.getWeather(51.5074, -0.1278)).thenReturn(londonWeather);

		// When/Then
		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("cities"))
			.andExpect(view().name("index"))
			.andExpect(model().attribute("cities", List.of(new WeatherController.CityWeather(paris, parisWeather),
					new WeatherController.CityWeather(london, londonWeather))));
	}

}
