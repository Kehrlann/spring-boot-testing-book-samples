package wf.garnier.spring.boot.test.ch2.weather.weather.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.City;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CityRepositoryTest {

    @Autowired
    private CityRepository cityRepository;

    @Test
    void shouldSaveAndRetrieveCity() {
        // given
        City paris = new City("Paris", "France", 48.8566, 2.3522);

        // when
        City savedCity = cityRepository.save(paris);

        // then
        assertThat(savedCity.getId()).isNotNull();
        assertThat(savedCity.getName()).isEqualTo("Paris");
        assertThat(savedCity.getLatitude()).isEqualTo(48.8566);
        assertThat(savedCity.getLongitude()).isEqualTo(2.3522);
    }

    @Test
    void shouldFindCityByName() {
        // given
        City paris = new City("Paris", "France", 48.8566, 2.3522);
        cityRepository.save(paris);

        // when
        Optional<City> found = cityRepository.findByName("Paris");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Paris");
    }

    @Test
    void shouldListAllCities() {
        // given
        City paris = new City("Paris", "France", 48.8566, 2.3522);
        City london = new City("London", "United Kingdom", 51.5074, -0.1278);
        cityRepository.saveAll(List.of(paris, london));

        // when
        List<City> cities = cityRepository.findAll();

        // then
        assertThat(cities).hasSize(2);
        assertThat(cities).extracting(City::getName).containsExactlyInAnyOrder("Paris", "London");
    }

    @Test
    void shouldDeleteCity() {
        // given
        City paris = new City("Paris", "France", 48.8566, 2.3522);
        City savedCity = cityRepository.save(paris);

        // when
        cityRepository.deleteById(savedCity.getId());

        // then
        assertThat(cityRepository.findById(savedCity.getId())).isEmpty();
    }
}