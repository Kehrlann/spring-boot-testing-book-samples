package wf.garnier.spring.boot.test.ch7.weather;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch7.weather.city.CityService;
import wf.garnier.spring.boot.test.ch7.weather.city.internal.CityRepository;
import wf.garnier.spring.boot.test.ch7.weather.city.internal.SelectedCityRepository;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;
import wf.garnier.spring.boot.test.ch7.weather.security.internal.DemoOidcUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

@SpringBootTest
@AutoConfigureMockMvc
class AdminTests {

	@Autowired
	MockMvcTester mvc;

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
	void listsLocalAndOidcUsers() {
		selectCity(new UserId("bob"), "Paris");
		// OIDC users are not stored in the app: they only exist through their selections
		var oidcUser = oidcUser("oidc-subject", "jane.doe@example.com");
		mvc.post()
			.uri("/api/city")
			.with(oidcLogin().oidcUser(oidcUser))
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content("{ \"id\": %s }".formatted(cityRepository.findByNameIgnoreCase("Tokyo").orElseThrow().getId()))
			.exchange()
			.assertThat()
			.hasStatus(HttpStatus.CREATED);

		var response = mvc.get().uri("/admin").exchange();

		assertThat(response).hasStatus(HttpStatus.OK)
			.bodyText()
			.containsSubsequence("bob", "Paris (France)", "oidc-subject", "Tokyo (Japan)");
	}

	@Test
	void oidcAdmin() {
		selectCity(new UserId("bob"), "Paris");

		var response = mvc.get()
			.uri("/admin")
			.with(oidcLogin()
					.idToken(token -> {
						token.subject("oidc-subject-id");
						token.claim(StandardClaimNames.EMAIL, "test@example.com");
					})
					.authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
			.exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyText().contains("bob", "Paris (France)");
	}

	@Test
	void oidcNonAdmin() {
		var response = mvc.get().uri("/admin").with(oidcLogin()).exchange();

		assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
	}

	@Test
	@WithMockUser
	void localNonAdmin() {
		var response = mvc.get().uri("/admin").exchange();

		assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
	}

	@Test
	void notLoggedIn() {
		var response = mvc.get().uri("/admin").exchange();

		assertThat(response).hasStatus(HttpStatus.FOUND).hasRedirectedUrl("/login");
	}

	private void selectCity(UserId user, String cityName) {
		cityService.addCityById(user, cityRepository.findByNameIgnoreCase(cityName).orElseThrow().getId());
	}

	private static DemoOidcUser oidcUser(String subject, String email, String... authorities) {
		var idToken = OidcIdToken.withTokenValue("id-token")
			.subject(subject)
			.issuedAt(Instant.now())
			.expiresAt(Instant.now().plusSeconds(60))
			.build();
		var userInfo = OidcUserInfo.builder().subject(subject).name("Jane Doe").email(email).build();
		return new DemoOidcUser(AuthorityUtils.createAuthorityList(authorities), idToken, userInfo);
	}

}
