package wf.garnier.spring.boot.test.ch7.weather.security.internal;

import wf.garnier.spring.boot.test.ch7.weather.security.WeatherUser;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class UserController {

	@GetMapping("/api/me")
	MeResponse me(@AuthenticationPrincipal WeatherUser weatherUser) {
		return new MeResponse(weatherUser.getUsername(), weatherUser.getUserEmail().toString());
	}

	record MeResponse(String username, String email) {
	}

}
