package wf.garnier.spring.boot.test.ch7.weather.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfiguration {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) {
		return http.authorizeHttpRequests(authz -> {
			authz.requestMatchers("/css/login.css").permitAll();
			authz.anyRequest().authenticated();
		})
			.formLogin(form -> form.loginPage("/login").permitAll())
			.logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
			.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
			.build();
	}

	@Bean
	LocalUserDetailsService localUserDetailsService() {
		return new LocalUserDetailsService(new LocalUser("alice", "pw", "alice@example.com", List.of("USER", "ADMIN")),
				new LocalUser("bob", "pw", "bob@example.com", List.of("USER")));
	}

}
