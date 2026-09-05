package wf.garnier.spring.boot.test.ch7.weather.security;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class LocalUserDetailsServiceTests {

	private final LocalUserDetailsService service = new LocalUserDetailsService(
			new LocalUser("alice", "secret", "alice@example.com", List.of("USER")));

	@Test
	void loadByUsername() {
		var alice = service.loadUserByUsername("alice");

		assertThat(alice.getUsername()).isEqualTo("alice");
		assertThat(alice.getUserEmail()).isEqualTo(new Email("alice@example.com"));
		assertThat(alice.getAuthorities()).map(GrantedAuthority::getAuthority).containsExactly("ROLE_USER");
		assertThat(alice.getPassword()).isEqualTo("{noop}secret");
	}

	@Test
	void usernameNotFound() {
		assertThatExceptionOfType(UsernameNotFoundException.class).isThrownBy(() -> service.loadUserByUsername("bob"));
	}

	@Test
	void eraseCredentials() {
		var alice = service.loadUserByUsername("alice");
		alice.eraseCredentials();
		assertThat(alice.getPassword()).isNull();

		assertThat(service.loadUserByUsername("alice").getPassword()).isNotNull();
	}

}
