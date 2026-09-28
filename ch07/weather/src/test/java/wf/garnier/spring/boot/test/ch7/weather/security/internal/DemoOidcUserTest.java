package wf.garnier.spring.boot.test.ch7.weather.security.internal;

import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch7.weather.security.Email;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import static org.assertj.core.api.Assertions.assertThat;

class DemoOidcUserTest {

	@Test
	void fromUserInfo() {
		var idToken = OidcIdToken.withTokenValue("id-token").subject("12345").build();
		var userInfo = OidcUserInfo.builder().subject("12345").name("Jane Doe").email("jane.doe@example.com").build();

		var user = new DemoOidcUser(AuthorityUtils.createAuthorityList("ROLE_USER"), idToken, userInfo);

		assertThat(user.getId()).isEqualTo(new UserId("oidc:12345"));
		assertThat(user.getUsername()).isEqualTo("Jane Doe");
		assertThat(user.getUserEmail()).isEqualTo(new Email("jane.doe@example.com"));
	}

}
