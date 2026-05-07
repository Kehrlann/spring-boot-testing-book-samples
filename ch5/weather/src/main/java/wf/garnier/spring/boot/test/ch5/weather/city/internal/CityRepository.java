package wf.garnier.spring.boot.test.ch5.weather.city.internal;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<CityEntity, Long> {

	Optional<CityEntity> findByNameIgnoreCase(String city);

}