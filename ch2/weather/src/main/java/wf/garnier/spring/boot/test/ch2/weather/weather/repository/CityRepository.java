package wf.garnier.spring.boot.test.ch2.weather.weather.repository;

import java.util.Optional;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.City;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {

	Optional<City> findByName(String name);

}