package wf.garnier.spring.boot.test.ch2.weather;

import java.util.List;
import wf.garnier.spring.boot.test.ch2.weather.selection.CityWeather;
import wf.garnier.spring.boot.test.ch2.weather.selection.SelectionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

	@GetMapping(value = "/api/weather")
	@ResponseBody
	public List<CityWeather> weather() {
		return selectionService.getWeatherInSelectedCities();
	}

	@PostMapping(value = "/city/add")
	public String addCity(String city) {
		selectionService.addCity(city);
		return "redirect:/";
	}

	@PostMapping(value = "/api/city")
	public ResponseEntity<Void> addCityApi(@RequestBody CityRequest req) {
		return selectionService.addCity(req.cityName()) ? ResponseEntity.status(HttpStatus.CREATED).build()
				: ResponseEntity.badRequest().build();
	}

	public record CityRequest(String cityName) {
	}

	@PostMapping(value = "/city/delete/{id}")
	public String addCity(@PathVariable long id) {
		selectionService.unselectCityById(id);
		return "redirect:/";
	}

	@DeleteMapping(value = "/api/city")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCity(@RequestBody CityRequest req) {
		selectionService.unselectCityByName(req.cityName());
	}

}
