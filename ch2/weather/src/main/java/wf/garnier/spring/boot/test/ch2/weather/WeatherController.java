package wf.garnier.spring.boot.test.ch2.weather;

import java.util.List;
import java.util.stream.Collectors;
import wf.garnier.spring.boot.test.ch2.weather.city.City;
import wf.garnier.spring.boot.test.ch2.weather.service.Selection;
import wf.garnier.spring.boot.test.ch2.weather.openmeteo.WeatherResponse;
import wf.garnier.spring.boot.test.ch2.weather.city.CityRepository;
import wf.garnier.spring.boot.test.ch2.weather.service.SelectionRepository;
import wf.garnier.spring.boot.test.ch2.weather.openmeteo.WeatherService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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

	@GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
	public String index(Model model) {
		var cities = cityRepository.findAll();
		var selectedCities = selectionRepository.findAll().stream().map(Selection::getCity).toList();
		var citiesWithWeather = selectedCities.stream()
			.map(selectedCity -> new CityWeather(selectedCity,
					weatherService.getWeather(selectedCity.getLatitude(), selectedCity.getLongitude())))
			.toList();
		cities.removeAll(selectedCities);
		model.addAttribute("cities", cities);
		model.addAttribute("preferredCities", citiesWithWeather);
		return "index";
	}

	@GetMapping(value = "/weather", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<CityWeather> weather() {
		return selectionRepository.findAll()
			.stream()
			.map(Selection::getCity)
			.map(city -> new CityWeather(city, weatherService.getWeather(city.getLatitude(), city.getLongitude())))
			.collect(Collectors.toList());
	}

	@PostMapping(value = "/city/add", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String addCity(String city) {
		cityRepository.findByNameIgnoreCase(city).ifPresent(c -> {
			if (selectionRepository.findByCity(c).isEmpty()) {
				selectionRepository.save(new Selection(c));
			}
		});
		return "redirect:/";
	}

	@PostMapping(value = "/city/add", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> addCityApi(@RequestBody SelectCityRequest req) {
		var city = cityRepository.findByNameIgnoreCase(req.cityName);
		if (city.isPresent()) {
			if (selectionRepository.findByCity(city.get()).isEmpty()) {
				selectionRepository.save(new Selection(city.get()));
				return ResponseEntity.status(HttpStatus.CREATED).build();
			}
		}
		return ResponseEntity.badRequest().build();
	}

	public record SelectCityRequest(String cityName) {
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
