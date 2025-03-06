package wf.garnier.spring.boot.test.ch2.weather.weather.repository;

import java.util.Optional;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.PreferredCity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferredCityRepository extends JpaRepository<PreferredCity, Long> {
    Optional<PreferredCity> findByCityId(Long cityId);
}