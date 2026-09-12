package wf.garnier.spring.boot.test.ch7.weather.city.internal;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SelectedCityRepositoryTests {

	private static final UserId ALICE = new UserId("alice");

	private static final UserId BOB = new UserId("bob");

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
		selectedCityRepository.save(new SelectedCity(ALICE, tokyo));

		List<CityEntity> unselectedCities = selectedCityRepository.findUnselectedCities(ALICE);

		assertThat(unselectedCities).doesNotContain(tokyo).contains(jakarta, paris);
	}

	@Test
	void unselectedCitiesByName() {
		selectedCityRepository.save(new SelectedCity(ALICE, jakarta));

		List<CityEntity> unselectedCities = selectedCityRepository.findUnselectedFilteredByCityNameIgnoringCase(ALICE,
				"jak");

		assertThat(unselectedCities).doesNotContain(jakarta)
			.map(CityEntity::getName)
			.containsExactlyInAnyOrder("Djakotomé", "Kamirenjaku");
	}

	@Test
	void shouldDeleteByCityId() {
		selectedCityRepository.save(new SelectedCity(ALICE, tokyo));
		selectedCityRepository.save(new SelectedCity(ALICE, jakarta));

		selectedCityRepository.deleteByUserAndCityId(ALICE, tokyo.getId());

		assertThat(selectedCityRepository.findAll()).hasSize(1);
		assertThat(selectedCityRepository.findByUserAndCity(ALICE, tokyo)).isEmpty();
		assertThat(selectedCityRepository.findByUserAndCity(ALICE, jakarta)).isPresent();
	}

	@Test
	void deleteCityByName() {
		selectedCityRepository.save(new SelectedCity(ALICE, tokyo));
		selectedCityRepository.save(new SelectedCity(ALICE, jakarta));

		selectedCityRepository.deleteByUserAndCityName(ALICE, "Tokyo");

		assertThat(selectedCityRepository.findAll()).hasSize(1);
		assertThat(selectedCityRepository.findByUserAndCity(ALICE, tokyo)).isEmpty();
		assertThat(selectedCityRepository.findByUserAndCity(ALICE, jakarta)).isPresent();
	}

	@Test
	void findByCity() {
		selectedCityRepository.save(new SelectedCity(ALICE, tokyo));

		Optional<SelectedCity> result = selectedCityRepository.findByUserAndCity(ALICE, tokyo);

		assertThat(result).map(SelectedCity::getCity).get().isEqualTo(tokyo);
	}

	@Test
	void orderByDateAddedAsc() {
		SelectedCity selectedTokyo = new SelectedCity(ALICE, tokyo);
		selectedTokyo.setDateAdded(Instant.now().minusSeconds(10));
		selectedCityRepository.save(selectedTokyo);

		SelectedCity selectedJakarta = new SelectedCity(ALICE, jakarta);
		selectedJakarta.setDateAdded(Instant.now());
		selectedCityRepository.save(selectedJakarta);

		List<SelectedCity> results = selectedCityRepository.findAllByUserOrderByDateAddedAsc(ALICE);

		assertThat(results).hasSize(2).map(SelectedCity::getCity).containsExactly(tokyo, jakarta);
	}

	@Test
	void selectionsAreScopedToTheirOwner() {
		selectedCityRepository.save(new SelectedCity(ALICE, tokyo));
		selectedCityRepository.save(new SelectedCity(BOB, jakarta));

		assertThat(selectedCityRepository.findAllByUserOrderByDateAddedAsc(ALICE)).map(SelectedCity::getCity)
			.containsExactly(tokyo);
		assertThat(selectedCityRepository.findUnselectedCities(ALICE)).doesNotContain(tokyo).contains(jakarta);
		assertThat(selectedCityRepository.findUnselectedCities(BOB)).doesNotContain(jakarta).contains(tokyo);
	}

	@Test
	void sameCityCanBeSelectedByTwoUsers() {
		selectedCityRepository.save(new SelectedCity(ALICE, tokyo));
		selectedCityRepository.save(new SelectedCity(BOB, tokyo));

		assertThat(selectedCityRepository.findByUserAndCity(ALICE, tokyo)).isPresent();
		assertThat(selectedCityRepository.findByUserAndCity(BOB, tokyo)).isPresent();
	}

}
