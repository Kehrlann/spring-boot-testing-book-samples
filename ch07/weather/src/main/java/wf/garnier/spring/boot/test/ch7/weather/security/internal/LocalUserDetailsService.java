package wf.garnier.spring.boot.test.ch7.weather.security.internal;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import wf.garnier.spring.boot.test.ch7.weather.security.UserId;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class LocalUserDetailsService implements UserDetailsService {

	private final Map<String, LocalUser> users;

	public LocalUserDetailsService(LocalUser... users) {
		this.users = Arrays.stream(users)
			.collect(
					Collectors.toMap(LocalUser::getUsername, Function.identity(), (a, b) -> a, ConcurrentHashMap::new));
	}

	@Override
	public LocalUser loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = users.get(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return new LocalUser(user);
	}

	public Optional<LocalUser> findById(UserId userId) {
		return Optional.ofNullable(this.users.get(userId.value())).map(LocalUser::new);
	}

}
