package wf.garnier.spring.boot.test.ch7.weather.weather.internal;

import java.util.List;

import wf.garnier.spring.boot.test.ch7.weather.security.UserId;
import wf.garnier.spring.boot.test.ch7.weather.weather.WeatherService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class WeatherController {

	private final WeatherService weatherService;

	public WeatherController(WeatherService weatherService) {
		this.weatherService = weatherService;
	}

	@GetMapping(value = "/api/weather")
	@ResponseBody
	public List<CityWeather> weather(Authentication authentication) {
		return weatherService.getWeatherInSelectedCities(UserId.of(authentication));
	}

}
