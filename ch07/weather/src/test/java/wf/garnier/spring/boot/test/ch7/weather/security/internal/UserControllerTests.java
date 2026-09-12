package wf.garnier.spring.boot.test.ch7.weather.security.internal;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;
import wf.garnier.spring.boot.test.ch7.weather.security.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.modulith.test.ModuleSlicing;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ModuleSlicing
@WebMvcTest(UserController.class)
class UserControllerTests {

	@Autowired
	MockMvcTester mvc;

	@Test
	@WithUserDetails("alice")
	void returnsCurrentUser() {
		var response = mvc.get().uri("/api/me").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
				{
				  "username": "alice",
				  "email": "alice@example.com"
				}
				""");
	}

	@Test
	void requiresAuthentication() {
		var response = mvc.get().uri("/api/me").exchange();

		assertThat(response).hasStatus(HttpStatus.FOUND);
	}

}
