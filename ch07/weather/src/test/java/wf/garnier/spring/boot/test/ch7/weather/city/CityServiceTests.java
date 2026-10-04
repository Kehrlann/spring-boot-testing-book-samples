package wf.garnier.spring.boot.test.ch7.weather.city;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch7.weather.city.internal.CityRepository;
import wf.garnier.spring.boot.test.ch7.weather.city.internal.SelectedCityRepository;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * The security module is included so that method security is enabled, and
 * {@code @PreAuthorize} is enforced on the {@link CityService}.
 */
@ApplicationModuleTest(extraIncludes = "security")
class CityServiceTests {

	private static final UserId ALICE = new UserId("alice");

	private static final UserId BOB = new UserId("bob");

	@Autowired
	CityService cityService;

	@Autowired
	CityRepository cityRepository;

	@Autowired
	SelectedCityRepository selectedCityRepository;

	@BeforeEach
	void setUp() {
		selectedCityRepository.deleteAll();
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void selectedCitiesByUser() {
		selectCity(BOB, "Paris");
		selectCity(ALICE, "Tokyo");
		selectCity(ALICE, "Jakarta");

		var citiesByUser = cityService.getSelectedCitiesByUser();

		assertThat(citiesByUser).containsOnlyKeys(ALICE, BOB);
		assertThat(citiesByUser.get(ALICE)).map(City::getName).containsExactly("Tokyo", "Jakarta");
		assertThat(citiesByUser.get(BOB)).map(City::getName).containsExactly("Paris");
	}

	@Test
	@WithMockUser(roles = "USER")
	void selectedCitiesByUserForbiddenForNonAdmins() {
		assertThatExceptionOfType(AccessDeniedException.class).isThrownBy(cityService::getSelectedCitiesByUser);
	}

	@Test
	void selectedCitiesByUserRequiresAuthentication() {
		assertThatExceptionOfType(AuthenticationCredentialsNotFoundException.class)
			.isThrownBy(cityService::getSelectedCitiesByUser);
	}

	private void selectCity(UserId user, String cityName) {
		cityService.addCityById(user, cityRepository.findByNameIgnoreCase(cityName).orElseThrow().getId());
	}

}
