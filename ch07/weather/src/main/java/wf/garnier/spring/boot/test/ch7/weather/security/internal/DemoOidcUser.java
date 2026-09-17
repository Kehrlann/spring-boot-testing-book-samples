package wf.garnier.spring.boot.test.ch7.weather.security.internal;

import java.util.Collection;

import wf.garnier.spring.boot.test.ch7.weather.security.Email;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;
import wf.garnier.spring.boot.test.ch7.weather.security.WeatherUser;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

public class DemoOidcUser extends DefaultOidcUser implements WeatherUser {

	private final UserId userId;

	public DemoOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken,
			OidcUserInfo userInfo) {
		super(authorities, idToken, userInfo);
		this.userId = new UserId("oidc:" + getSubject());
	}

	@Override
	public Email getUserEmail() {
		return new Email(getEmail());
	}

	@Override
	public UserId getId() {
		return userId;
	}

	@Override
	public String getUsername() {
		return getFullName();
	}

}
