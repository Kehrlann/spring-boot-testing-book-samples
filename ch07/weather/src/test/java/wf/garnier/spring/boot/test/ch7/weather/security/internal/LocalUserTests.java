package wf.garnier.spring.boot.test.ch7.weather.security.internal;

import java.util.List;

import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch7.weather.security.Email;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;

import org.springframework.security.core.GrantedAuthority;
import static org.assertj.core.api.Assertions.assertThat;

class LocalUserTests {

	@Test
	void constructor() {
		var user = new LocalUser("alice", "secret", "alice@example.com", List.of("USER", "ADMIN"));

		assertThat(user.getPassword()).isEqualTo("{noop}secret");
		assertThat(user.getAuthorities()).extracting(GrantedAuthority::getAuthority)
			.containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
		assertThat(user.getRoles()).containsExactlyInAnyOrder("USER", "ADMIN");
		assertThat(user.getEmail()).isEqualTo(new Email("alice@example.com"));
		assertThat(user.getId()).isEqualTo(new UserId("alice@example.com"));
	}

	@Test
	void rolesPrefix() {
		var user = new LocalUser("alice", "secret", "alice@example.com", List.of("ROLE_USER"));

		assertThat(user.getAuthorities()).extracting(GrantedAuthority::getAuthority).containsExactly("ROLE_USER");
	}

	@Test
	void encryptedPassword() {
		var user = new LocalUser("alice", "{bcrypt}$2a$12$hkXeSfwS7qP2xiIVA.9MdO0ggZilLdB7t7MqrSEz8e8mNE1/ys7Ba",
				"alice@example.com", List.of());

		assertThat(user.getPassword())
			.isEqualTo("{bcrypt}$2a$12$hkXeSfwS7qP2xiIVA.9MdO0ggZilLdB7t7MqrSEz8e8mNE1/ys7Ba");
	}

	@Test
	void eraseCredentials() {
		var user = new LocalUser("alice", "secret", "alice@example.com", List.of("USER"));

		user.eraseCredentials();

		assertThat(user.getPassword()).isNull();
	}

	@Test
	void copyConstructor() {
		var original = new LocalUser("alice", "secret", "alice@example.com", List.of("USER"));

		var copy = new LocalUser(original);

		assertThat(copy.getPassword()).isEqualTo(original.getPassword());
		assertThat(copy.getEmail()).isEqualTo(original.getEmail());
		assertThat(copy.getAuthorities()).isEqualTo(original.getAuthorities());
	}

	@Test
	void copyConstructorPassword() {
		var original = new LocalUser("alice", "{bcrypt}$2a$12$hkXeSfwS7qP2xiIVA.9MdO0ggZilLdB7t7MqrSEz8e8mNE1/ys7Ba",
				"alice@example.com", List.of("USER"));

		var copy = new LocalUser(original);

		assertThat(copy.getPassword())
			.isEqualTo("{bcrypt}$2a$12$hkXeSfwS7qP2xiIVA.9MdO0ggZilLdB7t7MqrSEz8e8mNE1/ys7Ba");
	}

}
