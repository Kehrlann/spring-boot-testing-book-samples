package wf.garnier.spring.boot.test.ch2.weather.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.City;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByName(String name);
}