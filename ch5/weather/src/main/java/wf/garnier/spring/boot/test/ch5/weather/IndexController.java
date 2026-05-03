package wf.garnier.spring.boot.test.ch5.weather;

import wf.garnier.spring.boot.test.ch5.weather.weather.WeatherService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

	private final WeatherService weatherService;

	public IndexController(WeatherService weatherService) {
		this.weatherService = weatherService;
	}

	@GetMapping(value = "/")
	public String index(Model model) {
		var citiesWithWeather = weatherService.getWeatherInSelectedCities();
		model.addAttribute("preferredCities", citiesWithWeather);
		return "index";
	}

}
