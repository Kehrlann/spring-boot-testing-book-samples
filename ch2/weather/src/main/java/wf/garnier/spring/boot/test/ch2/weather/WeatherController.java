package wf.garnier.spring.boot.test.ch2.weather;

import wf.garnier.spring.boot.test.ch2.weather.model.City;
import wf.garnier.spring.boot.test.ch2.weather.model.PreferredCity;
import wf.garnier.spring.boot.test.ch2.weather.model.WeatherResponse;
import wf.garnier.spring.boot.test.ch2.weather.repository.CityRepository;
import wf.garnier.spring.boot.test.ch2.weather.repository.PreferredCityRepository;
import wf.garnier.spring.boot.test.ch2.weather.service.WeatherService;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WeatherController {

	private final PreferredCityRepository preferredCityRepository;

	private final WeatherService weatherService;

	private final CityRepository cityRepository;

	public WeatherController(PreferredCityRepository preferredCityRepository, WeatherService weatherService,
			CityRepository cityRepository) {
		this.preferredCityRepository = preferredCityRepository;
		this.weatherService = weatherService;
		this.cityRepository = cityRepository;
	}

	@GetMapping("/")
	public String index(Model model) {
		var cities = cityRepository.findAll();
		model.addAttribute("cities", cities); // TODO other model
		var citiesWithWeather = preferredCityRepository.findAll()
			.stream()
			.map(preferredCity -> new CityWeather(preferredCity.getCity(),
					weatherService.getWeather(preferredCity.getCity().getLatitude(),
							preferredCity.getCity().getLongitude())))
			.toList();

		model.addAttribute("preferredCities", citiesWithWeather);
		return "index";
	}

	@PostMapping("/city/add")
	public String addCity(String city) {
		cityRepository.findByNameIgnoreCase(city).ifPresent(c -> {
			if (preferredCityRepository.findByCity(c).isEmpty()) {
				preferredCityRepository.save(new PreferredCity(c));
			}
		});
		return "redirect:/";
	}

	@PostMapping("/city/delete")
	@Transactional
	public String addCity(long id) {
		preferredCityRepository.deleteByCityId(id);
		return "redirect:/";
	}

	public record CityWeather(City city, WeatherResponse weather) {
	}

}
