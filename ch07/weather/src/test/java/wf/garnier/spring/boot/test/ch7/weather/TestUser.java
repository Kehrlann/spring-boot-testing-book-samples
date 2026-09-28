package wf.garnier.spring.boot.test.ch7.weather;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wf.garnier.spring.boot.test.ch7.weather.security.Email;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;
import wf.garnier.spring.boot.test.ch7.weather.security.WeatherUser;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * A {@link WeatherUser} test double with sensible defaults, built via {@link Builder}.
 *
 * <pre>{@code
 * Authentication auth = TestUser.withUsername("alice")
 *     .withEmail("alice@example.com")
 *     .withAuthority("ROLE_USER")
 *     .toAuthentication();
 * }</pre>
 */
public class TestUser implements WeatherUser {

	private final String id;

	private final String username;

	private final String email;

	public TestUser(String id, String username, String email) {
		this.id = id;
		this.username = username;
		this.email = email;
	}

	@Override
	public UserId getId() {
		return new UserId(id);
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public Email getUserEmail() {
		return new Email(email);
	}

	public static Builder withUsername(String username) {
		return new Builder().withUsername(username);
	}

	public static Builder withId(String id) {
		return new Builder().withId(id);
	}

	public static Builder withEmail(String email) {
		return new Builder().withEmail(email);
	}

	public static final class Builder {

		private String id = "~~ignored id~~";

		private String username = "Test User";

		private String email = "test@example.com";

		private Set<String> authorities = new HashSet<>();

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withUsername(String username) {
			this.username = username;
			return this;
		}

		public Builder withEmail(String email) {
			this.email = email;
			return this;
		}

		public Builder withAuthority(String authority) {
			this.authorities.add(authority);
			return this;
		}

		public Builder withAuthorities(String... authorities) {
			this.authorities.addAll(List.of(authorities));
			return this;
		}

		public TestUser build() {
			return new TestUser(id, username, email);
		}

		public Authentication toAuthentication() {
			return new TestingAuthenticationToken(build(), "~~ignored credentials~~",
					AuthorityUtils.createAuthorityList(authorities));
		}

	}

}
