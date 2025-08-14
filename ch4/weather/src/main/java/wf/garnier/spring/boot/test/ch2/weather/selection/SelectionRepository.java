package wf.garnier.spring.boot.test.ch2.weather.selection;

import java.util.List;
import java.util.Optional;
import wf.garnier.spring.boot.test.ch2.weather.city.City;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SelectionRepository extends JpaRepository<Selection, Long> {

	void deleteByCityId(long id);

	void deleteByCityName(String cityName);

	Optional<Selection> findByCity(City city);

	@Query("""
			SELECT c FROM City c
			    WHERE NOT EXISTS (
			        SELECT 1 FROM Selection s WHERE s.city.id = c.id
			    )
			ORDER BY c.name ASC
			""")
	List<City> findUnselectedCities();

	// TODO: limit
	@Query("""
			SELECT c FROM City c
			    WHERE NOT EXISTS (
			        SELECT 1 FROM Selection s WHERE s.city.id = c.id
			    )
			AND c.name ILIKE %:name%
			ORDER BY c.name ASC
			""")
	List<City> findUnselectedFilteredByCityNameIgnoringCase(String name);

	List<Selection> findAllByOrderByCityNameAsc();

}