package wf.garnier.spring.boot.test.ch7.weather.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTests {

	@Test
	void parseComponents() {
		var email = new Email("alice@example.com");

		assertThat(email.address()).isEqualTo("alice");
		assertThat(email.domain()).isEqualTo("example.com");
		assertThat(email.toString()).isEqualTo("alice@example.com");
	}

}
