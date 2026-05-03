package wf.garnier.spring.boot.test.ch5.weather.weather;

import java.util.List;

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
	public List<CityWeather> weather() {
		return weatherService.getWeatherInSelectedCities();
	}

}
