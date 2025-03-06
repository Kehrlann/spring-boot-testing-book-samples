package wf.garnier.spring.boot.test.ch2.weather.weather.controller;

import wf.garnier.spring.boot.test.ch2.weather.weather.model.City;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.WeatherResponse;
import wf.garnier.spring.boot.test.ch2.weather.weather.repository.PreferredCityRepository;
import wf.garnier.spring.boot.test.ch2.weather.weather.service.WeatherService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class WeatherController {

	private final PreferredCityRepository preferredCityRepository;

	private final WeatherService weatherService;

	public WeatherController(PreferredCityRepository preferredCityRepository, WeatherService weatherService) {
		this.preferredCityRepository = preferredCityRepository;
		this.weatherService = weatherService;
	}

	@GetMapping
	public String index(Model model) {
		var citiesWithWeather = preferredCityRepository.findAll()
			.stream()
			.map(preferredCity -> new CityWeather(preferredCity.getCity(),
					weatherService.getWeather(preferredCity.getCity().getLatitude(),
							preferredCity.getCity().getLongitude())))
			.toList();

		model.addAttribute("cities", citiesWithWeather);
		return "index";
	}

	record CityWeather(City city, WeatherResponse weather) {
	}

}
