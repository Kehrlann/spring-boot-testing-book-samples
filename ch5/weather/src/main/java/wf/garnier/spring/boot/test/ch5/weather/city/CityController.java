package wf.garnier.spring.boot.test.ch5.weather.city;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
class CityController {

	private final CityService cityService;

	public CityController(CityService cityService) {
		this.cityService = cityService;
	}

	@GetMapping(value = "/api/city")
	@ResponseBody
	public List<City> searchCities(@RequestParam(name = "q", required = true) String name) {
		return cityService.searchUnselectedCities(name);
	}

	@PostMapping(value = "/api/city")
	@ResponseStatus(HttpStatus.CREATED)
	public void addCityApi(@RequestBody CityRequest req) {
		cityService.addCityById(req.id());
	}

	@DeleteMapping(value = "/api/city/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCity(@PathVariable Long id) {
		cityService.unselectCityById(id);
	}

	public record CityRequest(long id) {
	}

}
