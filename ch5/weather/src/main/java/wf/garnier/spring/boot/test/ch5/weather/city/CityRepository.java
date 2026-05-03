package wf.garnier.spring.boot.test.ch5.weather.city;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface CityRepository extends JpaRepository<City, Long> {

	Optional<City> findByNameIgnoreCase(String city);

}