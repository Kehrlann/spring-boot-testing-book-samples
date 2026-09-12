package wf.garnier.spring.boot.test.ch7.weather.city;

import java.util.List;

import wf.garnier.spring.boot.test.ch7.weather.city.internal.CityEntity;
import wf.garnier.spring.boot.test.ch7.weather.city.internal.CityRepository;
import wf.garnier.spring.boot.test.ch7.weather.city.internal.SelectedCity;
import wf.garnier.spring.boot.test.ch7.weather.city.internal.SelectedCityRepository;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;

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

	public List<CityEntity> searchUnselectedCities(UserId owner, String name) {
		return selectedCityRepository.findUnselectedFilteredByCityNameIgnoringCase(owner, name);
	}

	public void addCityById(UserId owner, long cityId) {
		var city = cityRepository.findById(cityId).orElseThrow(() -> new CityNotFoundException(cityId));
		if (selectedCityRepository.findByUserAndCity(owner, city).isPresent()) {
			throw new CityAlreadySelectedException(cityId);
		}
		selectedCityRepository.save(new SelectedCity(owner, city));
	}

	@Transactional
	public void unselectCityById(UserId owner, long id) {
		selectedCityRepository.deleteByUserAndCityId(owner, id);
	}

	public List<? extends City> getSelectedCities(UserId owner) {
		return selectedCityRepository.findAllByUserOrderByDateAddedAsc(owner)
			.stream()
			.map(SelectedCity::getCity)
			.toList();
	}

}
