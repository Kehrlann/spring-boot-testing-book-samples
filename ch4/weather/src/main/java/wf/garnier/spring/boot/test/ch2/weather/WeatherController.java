package wf.garnier.spring.boot.test.ch2.weather;

import java.util.List;
import wf.garnier.spring.boot.test.ch2.weather.city.City;
import wf.garnier.spring.boot.test.ch2.weather.selection.CityWeather;
import wf.garnier.spring.boot.test.ch2.weather.selection.SelectionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class WeatherController {

	private final SelectionService selectionService;

	public WeatherController(SelectionService selectionService) {
		this.selectionService = selectionService;
	}

	@GetMapping(value = "/")
	public String index(Model model) {
		var cities = selectionService.findUnselectedCities();
		var citiesWithWeather = selectionService.getWeatherInSelectedCities();
		model.addAttribute("cities", cities);
		model.addAttribute("preferredCities", citiesWithWeather);
		return "index";
	}

	@GetMapping(value = "/api/city")
	@ResponseBody
	public List<City> searchCities(@RequestParam(name = "q", required = true) String name) {
		// TODO: DTO
		return selectionService.searchUnselectedCities(name);
	}

	@GetMapping(value = "/api/weather")
	@ResponseBody
	public List<CityWeather> weather() {
		return selectionService.getWeatherInSelectedCities();
	}

	@PostMapping(value = "/api/city")
	public ResponseEntity<String> addCityApi(@RequestBody CityRequest req) {
		return selectionService.addCityById(req.id()) ? ResponseEntity.status(HttpStatus.CREATED).build()
				: ResponseEntity.status(HttpStatus.CONFLICT).body("City already selected");
	}

	@DeleteMapping(value = "/api/city")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCity(@RequestBody CityRequest req) {
		selectionService.unselectCityById(req.id());
	}

	public record CityRequest(long id) {
	}

}
