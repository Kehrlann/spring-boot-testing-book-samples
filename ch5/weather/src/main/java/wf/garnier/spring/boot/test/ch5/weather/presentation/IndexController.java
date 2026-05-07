package wf.garnier.spring.boot.test.ch5.weather;

import wf.garnier.spring.boot.test.ch5.weather.weather.WeatherService;
import wf.garnier.spring.boot.test.ch5.weather.preferences.PreferencesService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

	private final WeatherService weatherService;
	private final PreferencesService preferencesService;

	public IndexController(WeatherService weatherService, PreferencesService preferencesService) {
		this.weatherService = weatherService;
		this.preferencesService = preferencesService;
	}

	@GetMapping(value = "/")
	public String index(Model model) {
		var citiesWithWeather = weatherService.getWeatherInSelectedCities();
		model.addAttribute("preferredCities", citiesWithWeather);
		model.addAttribute("preferences", preferencesService.getPreferences());
		return "index";
	}

}
