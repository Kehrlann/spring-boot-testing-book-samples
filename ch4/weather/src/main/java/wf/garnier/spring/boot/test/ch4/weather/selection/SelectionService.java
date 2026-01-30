package wf.garnier.spring.boot.test.ch4.weather.selection;

import java.util.List;

import wf.garnier.spring.boot.test.ch4.weather.city.City;
import wf.garnier.spring.boot.test.ch4.weather.city.CityRepository;
import wf.garnier.spring.boot.test.ch4.weather.openmeteo.WeatherService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SelectionService {

	private final SelectionRepository selectionRepository;

	private final WeatherService weatherService;

	private final CityRepository cityRepository;

	public SelectionService(SelectionRepository selectionRepository, WeatherService weatherService,
			CityRepository cityRepository) {
		this.selectionRepository = selectionRepository;
		this.weatherService = weatherService;
		this.cityRepository = cityRepository;
	}

	public List<CityWeather> getWeatherInSelectedCities() {
		return selectionRepository.findAllByOrderByCityNameAsc()
				.stream()
				.map(Selection::getCity)
				.map(city -> {
					var weatherData = weatherService.getCurrentWeather(city.getLatitude(), city.getLongitude());
					return new CityWeather(city, weatherData);
				}).toList();
	}

	public List<City> findUnselectedCities() {
		return selectionRepository.findUnselectedCities();
	}

	public List<City> searchUnselectedCities(String name) {
		return selectionRepository.findUnselectedFilteredByCityNameIgnoringCase(name);
	}

	public boolean addCityById(long cityId) {
		var city = cityRepository.findById(cityId);
		if (city.isPresent()) {
			if (selectionRepository.findByCity(city.get()).isEmpty()) {
				selectionRepository.save(new Selection(city.get()));
				return true;
			}
		}
		return false;
	}

	@Transactional
	public void unselectCityById(long id) {
		selectionRepository.deleteByCityId(id);
	}

}
