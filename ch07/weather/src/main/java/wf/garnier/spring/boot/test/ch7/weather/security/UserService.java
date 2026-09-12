package wf.garnier.spring.boot.test.ch7.weather.security;

import java.util.Optional;

import wf.garnier.spring.boot.test.ch7.weather.security.internal.LocalUserDetailsService;

import org.springframework.stereotype.Service;

@Service
public class UserService {

	private final LocalUserDetailsService userDetailsService;

	UserService(LocalUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public Optional<WeatherUser> findById(UserId userId) {
		return this.userDetailsService.findById(userId).map(WeatherUser.class::cast);
	}

}
