package wf.garnier.spring.boot.test.ch7.weather.security;

import org.junit.jupiter.api.Test;

import org.springframework.security.authentication.TestingAuthenticationToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class UserIdTests {

	@Test
	void fromPrincipal() {
		var userId = UserId.of(new TestingAuthenticationToken("alice", null));

		assertThat(userId).isEqualTo(new UserId("alice"));
		assertThat(userId).hasToString("alice");
	}

	@Test
	void rejectsEmptyValue() {
		assertThatIllegalArgumentException().isThrownBy(() -> new UserId(""));
		assertThatIllegalArgumentException().isThrownBy(() -> new UserId(null));
	}

	@Test
	void rejectsMissingPrincipal() {
		assertThatIllegalArgumentException().isThrownBy(() -> UserId.of(null));
	}

}
