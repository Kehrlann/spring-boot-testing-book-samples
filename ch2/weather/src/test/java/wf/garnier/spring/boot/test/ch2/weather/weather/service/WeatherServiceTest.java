package wf.garnier.spring.boot.test.ch2.weather.weather.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.WeatherResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WeatherServiceTest {

    @Autowired
    private WeatherService weatherService;

    @Test
    void shouldFetchWeatherForParis() {
        // Paris coordinates
        double latitude = 48.8566;
        double longitude = 2.3522;

        WeatherResponse weather = weatherService.getWeather(latitude, longitude);

        // We can't assert exact values as they change, but we can verify the response structure
        assertThat(weather).isNotNull();
        assertThat(weather.temperature()).isBetween(-50.0, 50.0); // Reasonable temperature range
        assertThat(weather.windspeed()).isGreaterThanOrEqualTo(0.0);
        assertThat(weather.getWeatherDescription()).isNotEqualTo("Unknown");
        
        // Log the actual values for manual verification
        System.out.println("[DEBUG_LOG] Weather in Paris:");
        System.out.println("[DEBUG_LOG] Temperature: " + weather.temperature() + "°C");
        System.out.println("[DEBUG_LOG] Wind Speed: " + weather.windspeed() + " km/h");
        System.out.println("[DEBUG_LOG] Weather: " + weather.getWeatherDescription());
    }
}