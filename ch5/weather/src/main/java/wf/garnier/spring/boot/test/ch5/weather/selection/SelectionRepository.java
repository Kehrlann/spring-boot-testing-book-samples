package wf.garnier.spring.boot.test.ch5.weather.selection;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface SelectionRepository extends JpaRepository<SelectedCity, Long> {

	void deleteByCityId(long id);

	void deleteByCityName(String cityName);

	Optional<SelectedCity> findByCity(City city);

	@Query("""
			SELECT c FROM City c
			    WHERE NOT EXISTS (
			        SELECT 1 FROM SelectedCity s WHERE s.city.id = c.id
			    )
			ORDER BY c.name ASC
			""")
	List<City> findUnselectedCities();

	@Query("""
			SELECT c FROM City c
			    WHERE NOT EXISTS (
			        SELECT 1 FROM SelectedCity s WHERE s.city.id = c.id
			    )
			AND c.name ILIKE %:name%
			ORDER BY c.name ASC
			""")
	List<City> findUnselectedFilteredByCityNameIgnoringCase(String name);

	List<SelectedCity> findAllByOrderByCityNameAsc();

}