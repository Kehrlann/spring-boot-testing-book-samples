package wf.garnier.spring.boot.test.ch7.weather.security.internal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.modulith.test.ModuleSlicing;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Implemented as two small app contexts
 */
class LoginControllerTest {

	@Nested
	@ModuleSlicing
	@WebMvcTest
	class InternalUsersOnly {

		@Autowired
		MockMvcTester mvc;

		@Test
		void loginPage() {
			var response = mvc.get().uri("/login").exchange();

			assertThat(response).hasStatus(HttpStatus.OK)
				.bodyText()
				.contains("Please sign in")
				.doesNotContain("login-oauth2");
		}

	}

	@Nested
	@ModuleSlicing
	@WebMvcTest
	class OidcUsers {

		@Autowired
		MockMvcTester mvc;

		@Test
		void loginPage() {
			var response = mvc.get().uri("/login").exchange();

			assertThat(response).hasStatus(HttpStatus.OK)
				.bodyText()
				.contains("Sign in with Dex")
				.contains("/oauth2/authorization/dex");
		}

		@TestConfiguration
		static class DexClientRegistration {

			@Bean
			InMemoryClientRegistrationRepository clientRegistrationRepository() {
			// @formatter:off
			return new InMemoryClientRegistrationRepository(
					ClientRegistration.withRegistrationId("dex")
						.clientId("base-client")
						.clientSecret("base-secret")
						.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
						.redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
						.authorizationUri("http://localhost:5556/auth")
						.tokenUri("http://localhost:5556/token")
						.clientName("Dex")
						.build());
			// @formatter:on
			}

		}

	}

}
