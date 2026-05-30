package wf.garnier.spring.boot.test.ch5.weather.city.internal;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SelectedCityRepositoryTest {

	@Autowired
	private SelectedCityRepository selectedCityRepository;

	@Autowired
	private CityRepository cityRepository;

	private CityEntity tokyo;

	private CityEntity jakarta;

	private CityEntity paris;

	@BeforeEach
	void setUp() {
		tokyo = cityRepository.findByNameIgnoreCase("Tokyo").orElseThrow();
		jakarta = cityRepository.findByNameIgnoreCase("Jakarta").orElseThrow();
		paris = cityRepository.findByNameIgnoreCase("Paris").orElseThrow();

		selectedCityRepository.deleteAll();
	}

	@Test
	void unselectedCities() {
		selectedCityRepository.save(new SelectedCity(tokyo));

		List<CityEntity> unselectedCities = selectedCityRepository.findUnselectedCities();

		assertThat(unselectedCities).doesNotContain(tokyo).contains(jakarta, paris);
	}

	@Test
	void unselectedCitiesByName() {
		selectedCityRepository.save(new SelectedCity(jakarta));

		List<CityEntity> unselectedCities = selectedCityRepository.findUnselectedFilteredByCityNameIgnoringCase("jak");

		assertThat(unselectedCities).doesNotContain(jakarta)
			.map(CityEntity::getName)
			.containsExactlyInAnyOrder("Djakotomé", "Kamirenjaku");
	}

	@Test
	void shouldDeleteByCityId() {
		selectedCityRepository.save(new SelectedCity(tokyo));
		selectedCityRepository.save(new SelectedCity(jakarta));

		selectedCityRepository.deleteByCityId(tokyo.getId());

		assertThat(selectedCityRepository.findAll()).hasSize(1);
		assertThat(selectedCityRepository.findByCity(tokyo)).isEmpty();
		assertThat(selectedCityRepository.findByCity(jakarta)).isPresent();
	}

	@Test
	void deleteCityByName() {
		selectedCityRepository.save(new SelectedCity(tokyo));
		selectedCityRepository.save(new SelectedCity(jakarta));

		selectedCityRepository.deleteByCityName("Tokyo");

		assertThat(selectedCityRepository.findAll()).hasSize(1);
		assertThat(selectedCityRepository.findByCity(tokyo)).isEmpty();
		assertThat(selectedCityRepository.findByCity(jakarta)).isPresent();
	}

	@Test
	void findByCity() {
		selectedCityRepository.save(new SelectedCity(tokyo));

		Optional<SelectedCity> result = selectedCityRepository.findByCity(tokyo);

		assertThat(result).map(SelectedCity::getCity).get().isEqualTo(tokyo);
	}

	@Test
	void orderByDateAddedAsc() {
		SelectedCity selectedTokyo = new SelectedCity(tokyo);
		selectedTokyo.setDateAdded(Instant.now().minusSeconds(10));
		selectedCityRepository.save(selectedTokyo);

		SelectedCity selectedJakarta = new SelectedCity(jakarta);
		selectedJakarta.setDateAdded(Instant.now());
		selectedCityRepository.save(selectedJakarta);

		List<SelectedCity> results = selectedCityRepository.findAllByOrderByDateAddedAsc();

		assertThat(results).hasSize(2).map(SelectedCity::getCity).containsExactly(tokyo, jakarta);
	}

}
