package wf.garnier.spring.boot.test.ch6.weather.city;

import java.util.List;

import wf.garnier.spring.boot.test.ch6.weather.city.internal.CityEntity;
import wf.garnier.spring.boot.test.ch6.weather.city.internal.CityRepository;
import wf.garnier.spring.boot.test.ch6.weather.city.internal.SelectedCity;
import wf.garnier.spring.boot.test.ch6.weather.city.internal.SelectedCityRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CityService {

	private final SelectedCityRepository selectedCityRepository;

	private final CityRepository cityRepository;

	CityService(SelectedCityRepository selectedCityRepository, CityRepository cityRepository) {
		this.selectedCityRepository = selectedCityRepository;
		this.cityRepository = cityRepository;
	}

	public List<CityEntity> searchUnselectedCities(String name) {
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

	public List<? extends City> getSelectedCities() {
		return selectedCityRepository.findAllByOrderByDateAddedAsc().stream().map(SelectedCity::getCity).toList();
	}

}
