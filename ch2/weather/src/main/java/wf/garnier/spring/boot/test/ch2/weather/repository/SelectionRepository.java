package wf.garnier.spring.boot.test.ch2.weather.repository;

import java.util.Optional;
import wf.garnier.spring.boot.test.ch2.weather.model.City;
import wf.garnier.spring.boot.test.ch2.weather.model.Selection;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SelectionRepository extends JpaRepository<Selection, Long> {

    void deleteByCityId(long id);

    Optional<Selection> findByCity(City city);
}