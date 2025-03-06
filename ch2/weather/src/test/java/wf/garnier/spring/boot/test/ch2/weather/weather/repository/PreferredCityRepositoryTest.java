package wf.garnier.spring.boot.test.ch2.weather.weather.repository;

import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.City;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.PreferredCity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PreferredCityRepositoryTest {

    @Autowired
    private PreferredCityRepository preferredCityRepository;

    @Autowired
    private CityRepository cityRepository;

    @Test
    void shouldFindPreferredCityByCityId() {
        // Given
        City paris = new City("Paris", "France", 48.8566, 2.3522);
        cityRepository.save(paris);
        PreferredCity preferredParis = new PreferredCity(paris);
        preferredCityRepository.save(preferredParis);

        // When
        var found = preferredCityRepository.findByCityId(paris.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCity().getName()).isEqualTo("Paris");
    }

    @Test
    void shouldNotFindPreferredCityForNonExistentCity() {
        // When
        var found = preferredCityRepository.findByCityId(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldNotFindPreferredCityForNonPreferredCity() {
        // Given
        City paris = new City("Paris", "France", 48.8566, 2.3522);
        cityRepository.save(paris);

        // When
        var found = preferredCityRepository.findByCityId(paris.getId());

        // Then
        assertThat(found).isEmpty();
    }
}
