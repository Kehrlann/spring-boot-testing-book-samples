package wf.garnier.spring.boot.test.ch7.weather.city.internal;

import java.util.List;
import java.util.Optional;

import wf.garnier.spring.boot.test.ch7.weather.security.UserId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SelectedCityRepository extends JpaRepository<SelectedCity, Long> {

	void deleteByUserAndCityId(UserId owner, long id);

	void deleteByUserAndCityName(UserId owner, String cityName);

	Optional<SelectedCity> findByUserAndCity(UserId owner, CityEntity city);

	@Query("""
			SELECT c FROM CityEntity c
			    WHERE NOT EXISTS (
			        SELECT 1 FROM SelectedCity s WHERE s.city.id = c.id AND s.user = :user
			    )
			ORDER BY c.name ASC
			""")
	List<CityEntity> findUnselectedCities(UserId user);

	@Query("""
			SELECT c FROM CityEntity c
			    WHERE NOT EXISTS (
			        SELECT 1 FROM SelectedCity s WHERE s.city.id = c.id AND s.user = :user
			    )
			AND c.name ILIKE %:name%
			ORDER BY c.name ASC
			""")
	List<CityEntity> findUnselectedFilteredByCityNameIgnoringCase(UserId user, String name);

	List<SelectedCity> findAllByUserOrderByDateAddedAsc(UserId user);

}
