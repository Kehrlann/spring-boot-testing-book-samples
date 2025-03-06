package wf.garnier.spring.boot.test.ch2.weather.repository;

import java.util.Optional;
import wf.garnier.spring.boot.test.ch2.weather.model.City;
import wf.garnier.spring.boot.test.ch2.weather.model.PreferredCity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferredCityRepository extends JpaRepository<PreferredCity, Long> {

    void deleteByCityId(long id);

    Optional<PreferredCity> findByCity(City city);
}