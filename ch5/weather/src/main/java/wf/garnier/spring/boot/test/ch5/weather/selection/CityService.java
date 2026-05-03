package wf.garnier.spring.boot.test.ch5.weather.selection;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CityService {

	private final SelectionRepository selectionRepository;

	private final CityRepository cityRepository;

	public CityService(SelectionRepository selectionRepository, CityRepository cityRepository) {
		this.selectionRepository = selectionRepository;
		this.cityRepository = cityRepository;
	}

	public List<City> searchUnselectedCities(String name) {
		return selectionRepository.findUnselectedFilteredByCityNameIgnoringCase(name);
	}

	public void addCityById(long cityId) {
		var city = cityRepository.findById(cityId).orElseThrow(() -> new CityNotFoundException(cityId));
		if (selectionRepository.findByCity(city).isPresent()) {
			throw new CityAlreadySelectedException(cityId);
		}
		selectionRepository.save(new SelectedCity(city));
	}

	@Transactional
	public void unselectCityById(long id) {
		selectionRepository.deleteByCityId(id);
	}

	public List<City> getSelectedCities() {
		return selectionRepository.findAllByOrderByCityNameAsc().stream().map(SelectedCity::getCity).toList();
	}

}
