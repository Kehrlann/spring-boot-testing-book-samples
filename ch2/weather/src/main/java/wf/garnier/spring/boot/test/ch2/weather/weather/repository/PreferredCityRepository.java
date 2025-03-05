package wf.garnier.spring.boot.test.ch2.weather.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.PreferredCity;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.City;

import java.util.Optional;

public interface PreferredCityRepository extends JpaRepository<PreferredCity, Long> {
    Optional<PreferredCity> findByCityId(Long cityId);
}