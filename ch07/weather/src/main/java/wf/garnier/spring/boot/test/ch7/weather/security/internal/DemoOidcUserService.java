package wf.garnier.spring.boot.test.ch7.weather.security.internal;

import java.util.Collection;

import org.jspecify.annotations.NonNull;
import wf.garnier.spring.boot.test.ch7.weather.security.Email;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
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
		return new DemoOidcUser(getAuthorities(user), user.getIdToken(), user.getUserInfo());
	}

	private static @NonNull Collection<? extends GrantedAuthority> getAuthorities(OidcUser user) {
		if (user.getEmail() == null) {
			return AuthorityUtils.NO_AUTHORITIES;
		}
		return new Email(user.getEmail()).domain().equals("admin.example.com")
				? AuthorityUtils.createAuthorityList("ROLE_ADMIN") : AuthorityUtils.NO_AUTHORITIES;
	}

}
