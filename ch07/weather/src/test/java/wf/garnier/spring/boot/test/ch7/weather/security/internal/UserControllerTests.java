package wf.garnier.spring.boot.test.ch7.weather.security.internal;

import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch7.weather.TestUser;
import wf.garnier.spring.boot.test.ch7.weather.security.Email;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;
import wf.garnier.spring.boot.test.ch7.weather.security.WeatherUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.modulith.test.ModuleSlicing;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;

@ModuleSlicing
@WebMvcTest(value = UserController.class)
class UserControllerTests {

	@Autowired
	MockMvcTester mvc;

	@Test
	@WithUserDetails("alice")
	void returnsCurrentUser() {
		var response = mvc.get().uri("/api/me").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
				{
				  "username": "alice",
				  "email": "alice@example.com"
				}
				""");
	}

	@Test
	void rejectsRawOidc() {
		var response = mvc.get()
			.uri("/api/me")
			.with(SecurityMockMvcRequestPostProcessors.oidcLogin()
				.idToken(token -> token.claim("name", "Test User").claim("email", "test@example.com")))
			.exchange();

		// Here we are using a "plain" OidcUser, which does not implement our common
		// interface WeatherUser, so the access is denied, as only WeatherUser are
		// allowed into our app.
		//
		// It's not a redirect to /login because the user is logged in, not anonymous,
		// and so this is in a invalid authentication rather than no auth that requires
		// login.
		assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
	}

	@Test
	void returnsCurrentWeatherUser() {
		var testUser = new WeatherUser() {
			@Override
			public UserId getId() {
				return new UserId("~~ignored id~~");
			}

			@Override
			public String getUsername() {
				return "Test User";
			}

			@Override
			public Email getUserEmail() {
				return new Email("test@example.com");
			}
		};
		var response = mvc.get()
			.uri("/api/me")
			.with(SecurityMockMvcRequestPostProcessors.authentication(
					new TestingAuthenticationToken(testUser, "~~ignored password~~", AuthorityUtils.NO_AUTHORITIES)))
			.exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
				{
				  "username": "Test User",
				  "email": "test@example.com"
				}
				""");
	}

	@Test
	void returnsCurrentWeatherUserUtilityClass() {
		var testAuthn = TestUser.withUsername("Test User").withEmail("test@example.com").toAuthentication();
		var response = mvc.get()
			.uri("/api/me")
			.with(SecurityMockMvcRequestPostProcessors.authentication(testAuthn))
			.exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
				{
				  "username": "Test User",
				  "email": "test@example.com"
				}
				""");
	}

	@Test
	void requiresAuthentication() {
		var response = mvc.get().uri("/api/me").exchange();

		assertThat(response).hasStatus(HttpStatus.FOUND).hasRedirectedUrl("/login");
	}

}
