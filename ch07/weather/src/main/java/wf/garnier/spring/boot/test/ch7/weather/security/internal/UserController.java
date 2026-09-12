package wf.garnier.spring.boot.test.ch7.weather.security.internal;

import wf.garnier.spring.boot.test.ch7.weather.security.UserId;
import wf.garnier.spring.boot.test.ch7.weather.security.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class UserController {

	private final UserService userService;

	UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/api/me")
	MeResponse me(Authentication authentication) {
		var user = userService.findById(UserId.of(authentication))
			.orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + authentication.getName()));
		return new MeResponse(user.getUsername(), user.getEmail().toString());
	}

	record MeResponse(String username, String email) {
	}

}
