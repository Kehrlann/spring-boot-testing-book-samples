package wf.garnier.spring.boot.test.ch2.weather.weather;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch2.weather.weather.repository.CityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WeatherApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CityRepository cityRepository;

    @BeforeEach
    void setUp() {
        cityRepository.deleteAll();
    }

    @Test
    void shouldShowRealWeatherForParis() throws Exception {
        // when adding Paris
        mockMvc.perform(post("/cities")
                .param("name", "Paris")
                .param("latitude", "48.8566")
                .param("longitude", "2.3522"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"));

        // then weather is displayed
        mockMvc.perform(get("/"))
               .andExpect(status().isOk())
               .andExpect(content().string(containsString("Paris")))
               // Check for weather data structure without asserting exact values
               .andExpect(content().string(containsString("Temperature: <span>")))
               .andExpect(content().string(containsString("</span>°C")))
               .andExpect(content().string(containsString("Wind Speed: <span>")))
               .andExpect(content().string(containsString("</span> km/h")))
               .andExpect(content().string(containsString("Weather: <span>")))
               // Log actual values for manual verification
               .andDo(result -> {
                   System.out.println("[DEBUG_LOG] Response content:");
                   System.out.println(result.getResponse().getContentAsString());
               });

        // and city is in database
        assertThat(cityRepository.findByName("Paris")).isPresent();
    }
}