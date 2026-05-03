package wf.garnier.spring.boot.test.ch5.weather.selection;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CityService {

	private final SelectedCityRepository selectedCityRepository;

	private final CityRepository cityRepository;

	public CityService(SelectedCityRepository selectedCityRepository, CityRepository cityRepository) {
		this.selectedCityRepository = selectedCityRepository;
		this.cityRepository = cityRepository;
	}

	public List<City> searchUnselectedCities(String name) {
		return selectedCityRepository.findUnselectedFilteredByCityNameIgnoringCase(name);
	}

	public void addCityById(long cityId) {
		var city = cityRepository.findById(cityId).orElseThrow(() -> new CityNotFoundException(cityId));
		if (selectedCityRepository.findByCity(city).isPresent()) {
			throw new CityAlreadySelectedException(cityId);
		}
		selectedCityRepository.save(new SelectedCity(city));
	}

	@Transactional
	public void unselectCityById(long id) {
		selectedCityRepository.deleteByCityId(id);
	}

	public List<City> getSelectedCities() {
		return selectedCityRepository.findAllByOrderByCityNameAsc().stream().map(SelectedCity::getCity).toList();
	}

}
