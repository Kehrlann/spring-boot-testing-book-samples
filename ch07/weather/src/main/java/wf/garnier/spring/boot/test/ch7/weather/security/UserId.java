package wf.garnier.spring.boot.test.ch7.weather.security;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

/**
 * Identifies a user across the whole application.
 * <p>
 * This is the only user-related type that other modules are expected to store. It
 * deliberately carries no profile data, no credentials and no roles, so that modules
 * owning per-user data do not depend on how users are authenticated.
 */
@Embeddable
public record UserId(String value) implements Serializable {

	public UserId {
		Assert.hasText(value, "user id must not be empty");
	}

	/**
	 * Derives the {@link UserId} of the currently authenticated user. Controllers can
	 * obtain a {@link org.springframework.security.core.Authentication} straight from
	 * Spring MVC, which means no module needs to depend on Spring Security to know who is
	 * calling.
	 */
	public static UserId of(Authentication authentication) {
		Assert.notNull(authentication, "authentication must not be null");
		return new UserId(authentication.getName());
	}

	@Override
	public String toString() {
		return this.value;
	}

}
