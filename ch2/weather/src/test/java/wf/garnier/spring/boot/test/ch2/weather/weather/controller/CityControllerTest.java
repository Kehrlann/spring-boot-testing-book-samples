package wf.garnier.spring.boot.test.ch2.weather.weather.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.City;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.PreferredCity;
import wf.garnier.spring.boot.test.ch2.weather.weather.repository.CityRepository;
import wf.garnier.spring.boot.test.ch2.weather.weather.repository.PreferredCityRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CityController.class)
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CityRepository cityRepository;

    @MockBean
    private PreferredCityRepository preferredCityRepository;

    @Test
    void shouldListAllCities() throws Exception {
        // Given
        City paris = new City("Paris", "France", 48.8566, 2.3522);
        when(cityRepository.findAll()).thenReturn(List.of(paris));

        // When/Then
        mockMvc.perform(get("/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Paris"))
                .andExpect(jsonPath("$[0].country").value("France"));
    }

    @Test
    void shouldAddCityToPreferred() throws Exception {
        // Given
        City paris = new City("Paris", "France", 48.8566, 2.3522);
        when(cityRepository.findById(1L)).thenReturn(Optional.of(paris));
        when(preferredCityRepository.findByCityId(1L)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(post("/cities/1/prefer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(preferredCityRepository).save(any(PreferredCity.class));
    }

    @Test
    void shouldNotAddAlreadyPreferredCity() throws Exception {
        // Given
        City paris = new City("Paris", "France", 48.8566, 2.3522);
        PreferredCity preferredParis = new PreferredCity(paris);
        when(cityRepository.findById(1L)).thenReturn(Optional.of(paris));
        when(preferredCityRepository.findByCityId(1L)).thenReturn(Optional.of(preferredParis));

        // When/Then
        mockMvc.perform(post("/cities/1/prefer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(preferredCityRepository, never()).save(any());
    }

    @Test
    void shouldReturn404WhenAddingNonExistentCity() throws Exception {
        // Given
        when(cityRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(post("/cities/999/prefer"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRemoveCityFromPreferred() throws Exception {
        // Given
        City paris = new City("Paris", "France", 48.8566, 2.3522);
        PreferredCity preferredParis = new PreferredCity(paris);
        when(preferredCityRepository.findByCityId(1L)).thenReturn(Optional.of(preferredParis));

        // When/Then
        mockMvc.perform(post("/cities/1/unprefer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(preferredCityRepository).delete(preferredParis);
    }

    @Test
    void shouldHandleRemovingNonPreferredCity() throws Exception {
        // Given
        when(preferredCityRepository.findByCityId(1L)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(post("/cities/1/unprefer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(preferredCityRepository, never()).delete(any());
    }
}