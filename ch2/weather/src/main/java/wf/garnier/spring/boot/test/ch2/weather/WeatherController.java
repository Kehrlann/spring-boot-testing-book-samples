package wf.garnier.spring.boot.test.ch2.weather;

import java.util.List;
import wf.garnier.spring.boot.test.ch2.weather.selection.CityWeather;
import wf.garnier.spring.boot.test.ch2.weather.selection.SelectionRepository;
import wf.garnier.spring.boot.test.ch2.weather.selection.SelectionService;

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

	private final SelectionService selectionService;

	public WeatherController(SelectionRepository selectionRepository, SelectionService selectionService) {
		this.selectionRepository = selectionRepository;
		this.selectionService = selectionService;
	}

	@GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
	public String index(Model model) {
		var cities = selectionService.findUnselectedCities();
		var citiesWithWeather = selectionService.getWeatherInSelectedCities();
		model.addAttribute("cities", cities);
		model.addAttribute("preferredCities", citiesWithWeather);
		return "index";
	}

	@GetMapping(value = "/weather", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<CityWeather> weather() {
		return selectionService.getWeatherInSelectedCities();
	}

	@PostMapping(value = "/city/add", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String addCity(String city) {
		selectionService.addCity(city);
		return "redirect:/";
	}

	@PostMapping(value = "/city/add", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> addCityApi(@RequestBody SelectCityRequest req) {
		return selectionService.addCity(req.cityName) ? ResponseEntity.status(HttpStatus.CREATED).build()
				: ResponseEntity.badRequest().build();
	}

	public record SelectCityRequest(String cityName) {
	}

	@PostMapping("/city/delete")
	@Transactional
	public String addCity(long id) {
		selectionRepository.deleteByCityId(id);
		return "redirect:/";
	}

}
