package wf.garnier.spring.boot.test.ch2.weather;

import java.util.List;
import wf.garnier.spring.boot.test.ch2.weather.model.City;
import wf.garnier.spring.boot.test.ch2.weather.model.Selection;
import wf.garnier.spring.boot.test.ch2.weather.model.WeatherResponse;
import wf.garnier.spring.boot.test.ch2.weather.repository.CityRepository;
import wf.garnier.spring.boot.test.ch2.weather.repository.SelectionRepository;
import wf.garnier.spring.boot.test.ch2.weather.service.WeatherService;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WeatherController {

	private final SelectionRepository selectionRepository;

	private final WeatherService weatherService;

	private final CityRepository cityRepository;

	public WeatherController(SelectionRepository selectionRepository, WeatherService weatherService,
							 CityRepository cityRepository) {
		this.selectionRepository = selectionRepository;
		this.weatherService = weatherService;
		this.cityRepository = cityRepository;
	}

	@GetMapping("/")
	public String index(Model model) {
		var cities = cityRepository.findAll();
		var selectedCities = selectionRepository.findAll()
				.stream().map(Selection::getCity)
				.toList();
		var citiesWithWeather = selectedCities
			.stream()
			.map(selectedCity -> new CityWeather(selectedCity,
					weatherService.getWeather(selectedCity.getLatitude(),
							selectedCity.getLongitude())))
			.toList();
		cities.removeAll(selectedCities);
		model.addAttribute("cities", cities);
		model.addAttribute("preferredCities", citiesWithWeather);
		return "index";
	}

	@PostMapping("/city/add")
	public String addCity(String city) {
		cityRepository.findByNameIgnoreCase(city).ifPresent(c -> {
			if (selectionRepository.findByCity(c).isEmpty()) {
				selectionRepository.save(new Selection(c));
			}
		});
		return "redirect:/";
	}

	@PostMapping("/city/delete")
	@Transactional
	public String addCity(long id) {
		selectionRepository.deleteByCityId(id);
		return "redirect:/";
	}

	public record CityWeather(City city, WeatherResponse weather) {
	}

}
