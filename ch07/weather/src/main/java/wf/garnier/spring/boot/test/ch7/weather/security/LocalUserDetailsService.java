package wf.garnier.spring.boot.test.ch7.weather.security;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class LocalUserDetailsService implements UserDetailsService {

	private final Map<String, LocalUser> users;

	private final PasswordEncoder passwordEncoder;

	public LocalUserDetailsService(LocalUser... users) {
		this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

		this.users = Arrays.stream(users).collect(Collectors.toMap(LocalUser::getUsername, Function.identity()));
	}

	@Override
	public LocalUser loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = users.get(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return new LocalUser(user);
	}

}
