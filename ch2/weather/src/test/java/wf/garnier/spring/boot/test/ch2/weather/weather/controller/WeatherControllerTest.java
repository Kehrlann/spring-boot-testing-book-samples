package wf.garnier.spring.boot.test.ch2.weather.weather.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.City;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.WeatherResponse;
import wf.garnier.spring.boot.test.ch2.weather.weather.repository.CityRepository;
import wf.garnier.spring.boot.test.ch2.weather.weather.service.WeatherService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CityRepository cityRepository;

    @MockBean
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        cityRepository.deleteAll();
    }

    @Test
    void shouldShowEmptyListOfCities() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(model().attribute("cities", java.util.List.of()));
    }

    @Test
    void shouldAddNewCity() throws Exception {
        // given
        when(weatherService.getWeather(anyDouble(), anyDouble()))
            .thenReturn(new WeatherResponse(20.0, 5.0, 0));

        // when
        mockMvc.perform(post("/cities")
                .param("name", "Paris")
                .param("latitude", "48.8566")
                .param("longitude", "2.3522"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"));

        // then
        var cities = cityRepository.findAll();
        assertThat(cities).hasSize(1);
        assertThat(cities.get(0).getName()).isEqualTo("Paris");
    }

    @Test
    void shouldDeleteCity() throws Exception {
        // given
        var paris = cityRepository.save(new City("Paris", "France", 48.8566, 2.3522));
        when(weatherService.getWeather(anyDouble(), anyDouble()))
            .thenReturn(new WeatherResponse(20.0, 5.0, 0));

        // when
        mockMvc.perform(post("/cities/{id}/delete", paris.getId()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"));

        // then
        assertThat(cityRepository.findAll()).isEmpty();
    }

    @Test
    void shouldShowWeatherForCity() throws Exception {
        // given
        var paris = cityRepository.save(new City("Paris", "France", 48.8566, 2.3522));
        when(weatherService.getWeather(48.8566, 2.3522))
            .thenReturn(new WeatherResponse(20.0, 5.0, 0));

        // when & then
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("cities"))
               .andExpect(content().string(org.hamcrest.Matchers.containsString("Temperature: <span>20.0</span>°C")))
               .andExpect(content().string(org.hamcrest.Matchers.containsString("Wind Speed: <span>5.0</span> km/h")))
               .andExpect(content().string(org.hamcrest.Matchers.containsString("Weather: <span>Clear sky</span>")));
    }
}
