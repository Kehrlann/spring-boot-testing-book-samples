package wf.garnier.spring.boot.test.ch7.weather.security.internal;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch7.weather.security.Email;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;

import static org.assertj.core.api.Assertions.assertThat;

class DemoOidcUserServiceTests {

	// No user-info-uri: OidcUserService builds the user from the ID token, without
	// any HTTP call.
	private final ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("dex")
		.clientId("weather-app")
		.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
		.redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
		.authorizationUri("https://idp.example.com/authorize")
		.tokenUri("https://idp.example.com/token")
		.scope("openid", "email", "profile")
		.build();

	private final DemoOidcUserService service = new DemoOidcUserService();

	@Test
	void loadUser() {
		var user = service.loadUser(userRequest(idToken().claim("email", "jane.doe@example.com").build()));

		assertThat(user).isInstanceOfSatisfying(DemoOidcUser.class, demoUser -> {
			assertThat(demoUser.getId()).isEqualTo(new UserId("oidc:12345"));
			assertThat(demoUser.getUsername()).isEqualTo("Jane Doe");
			assertThat(demoUser.getUserEmail()).isEqualTo(new Email("jane.doe@example.com"));
		});
	}

	@Test
	void adminDomain() {
		var user = service.loadUser(userRequest(idToken().claim("email", "jane.doe@admin.example.com").build()));

		assertThat(user.getAuthorities()).map(GrantedAuthority::getAuthority).containsExactly("ROLE_ADMIN");
	}

	@Test
	void regularDomain() {
		var user = service.loadUser(userRequest(idToken().claim("email", "jane.doe@example.com").build()));

		assertThat(user.getAuthorities()).isEmpty();
	}

	@Test
	void adminSubdomain() {
		var user = service.loadUser(userRequest(idToken().claim("email", "jane.doe@other.admin.example.com").build()));

		assertThat(user.getAuthorities()).isEmpty();
	}

	@Test
	void noEmail() {
		var user = service.loadUser(userRequest(idToken().build()));

		assertThat(user.getAuthorities()).isEmpty();
	}

	private static OidcIdToken.Builder idToken() {
		return OidcIdToken.withTokenValue("id-token")
			.subject("12345")
			.claim("name", "Jane Doe")
			.issuedAt(Instant.now())
			.expiresAt(Instant.now().plusSeconds(60));
	}

	private OidcUserRequest userRequest(OidcIdToken idToken) {
		var accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "access-token", Instant.now(),
				Instant.now().plusSeconds(60));
		return new OidcUserRequest(clientRegistration, accessToken, idToken);
	}

}
