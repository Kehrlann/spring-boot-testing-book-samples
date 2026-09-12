package wf.garnier.spring.boot.test.ch7.weather.security;

import java.util.List;

import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch7.weather.security.internal.LocalUser;
import wf.garnier.spring.boot.test.ch7.weather.security.internal.LocalUserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceTests {

	private static final UserId ALICE = new UserId("alice");

	private final UserService userService = new UserService(
			new LocalUserDetailsService(new LocalUser("alice", "pw", "alice@example.com", List.of("USER"))));

	@Test
	void findById() {
		assertThat(userService.findById(ALICE)).get()
			.extracting(WeatherUser::getUsername, WeatherUser::getEmail)
			.containsExactly("alice", new Email("alice@example.com"));
	}

	@Test
	void findMissingUser() {
		assertThat(userService.findById(new UserId("carol"))).isEmpty();
	}

}
