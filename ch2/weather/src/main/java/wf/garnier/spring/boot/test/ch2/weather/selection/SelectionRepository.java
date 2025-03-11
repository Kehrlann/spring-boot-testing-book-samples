package wf.garnier.spring.boot.test.ch2.weather.selection;

import java.util.Optional;
import wf.garnier.spring.boot.test.ch2.weather.city.City;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SelectionRepository extends JpaRepository<Selection, Long> {

    void deleteByCityId(long id);

    Optional<Selection> findByCity(City city);
}