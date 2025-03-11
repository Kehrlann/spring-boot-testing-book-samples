package wf.garnier.spring.boot.test.ch2.weather.city;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByName(String name);

    Optional<City> findByNameIgnoreCase(String city);
}