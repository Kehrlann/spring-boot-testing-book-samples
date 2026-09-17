package wf.garnier.spring.boot.test.ch7.weather.security.internal;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfiguration {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http,
			ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository) {
		http.authorizeHttpRequests(authz -> {
			authz.requestMatchers("/css/login.css").permitAll();
			authz.anyRequest().authenticated();
		})
			.formLogin(form -> form.loginPage("/login").permitAll())
			.logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
			// Store the CSRF token in a cookie, which the JavaScript
			// frontend reads and sends back in the X-XSRF-TOKEN header.
			// See:
			// https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript-spa
			.csrf(CsrfConfigurer::spa);

		// Spring Boot only contributes a ClientRegistrationRepository when at least one
		// OAuth2 client is configured, which here happens in the "docker" profile, where
		// Dex runs as part of the Docker Compose setup. Without Dex, there is nothing to
		// log in to, and OAuth2 login stays off.
		clientRegistrationRepository
			.ifAvailable(_ -> http.oauth2Login(oauth2 -> oauth2.loginPage("/login").permitAll()));

		return http.build();
	}

	@Bean
	LocalUserDetailsService localUserDetailsService() {
		//@formatter:off
		return new LocalUserDetailsService(
				new LocalUser("alice", "pw", "alice@example.com", List.of("USER", "ADMIN")),
				new LocalUser("bob", "pw", "bob@example.com", List.of("USER")),
				new LocalUser("carol", "pw", "carol@example.com", List.of("USER")),
				new LocalUser("daniel", "pw", "daniel@example.com", List.of("USER"))
			);
		//@formatter:on
	}

}
