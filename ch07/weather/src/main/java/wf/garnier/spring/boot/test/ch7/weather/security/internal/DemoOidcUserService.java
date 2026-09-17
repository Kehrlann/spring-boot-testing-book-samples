package wf.garnier.spring.boot.test.ch7.weather.security.internal;

import org.jspecify.annotations.NonNull;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
class DemoOidcUserService extends OidcUserService {

	@Override
	public @NonNull OidcUser loadUser(@NonNull OidcUserRequest userRequest) throws OAuth2AuthenticationException {
		var user = super.loadUser(userRequest);
		return new DemoOidcUser(user.getAuthorities(), user.getIdToken(), user.getUserInfo());
	}

}
