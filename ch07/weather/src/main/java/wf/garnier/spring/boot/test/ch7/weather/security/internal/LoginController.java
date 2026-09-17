package wf.garnier.spring.boot.test.ch7.weather.security.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Renders a custom login page. The page is written out by hand, because the application
 * does not use a template engine.
 */
@Controller
class LoginController {

	private static final String PAGE = """
			<!DOCTYPE html>
			<html lang="en">
			<head>
			    <meta charset="UTF-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
			    <title>Please sign in</title>
			    <link href="/css/login.css" rel="stylesheet">
			</head>
			<body>
			<form class="login-form" method="post" action="/login">
			    <h1>Please sign in</h1>
			%s    <label for="username">Username</label>
			    <input type="text" id="username" name="username" autofocus required>
			    <label for="password">Password</label>
			    <input type="password" id="password" name="password" required>
			    <input type="hidden" name="%s" value="%s">
			    <button type="submit">Sign in</button>
			%s</form>
			</body>
			</html>
			""";

	private static final String ERROR_MESSAGE = """
			    <p class="message message-error">Invalid username or password.</p>
			""";

	private static final String LOGOUT_MESSAGE = """
			    <p class="message message-logout">You have been signed out.</p>
			""";

	/**
	 * Link starting the {@code authorization_code} flow. Spring Security exposes one such
	 * endpoint per client registration.
	 */
	private static final String OAUTH2_LINK = """
			    <p class="login-separator">or</p>
			    <a class="login-oauth2" href="/oauth2/authorization/%s">Sign in with %s</a>
			""";

	private final List<ClientRegistration> clientRegistrations;

	/**
	 * The {@code ClientRegistrationRepository} is only present when an OAuth2 client is
	 * configured, which is why it is injected through an {@link ObjectProvider}. When
	 * there is none, the page only offers the username and password form.
	 */
	LoginController(ObjectProvider<InMemoryClientRegistrationRepository> clientRegistrationRepository) {
		var registrations = new ArrayList<ClientRegistration>();
		clientRegistrationRepository.ifAvailable(repository -> repository.forEach(registrations::add));
		this.clientRegistrations = List.copyOf(registrations);
	}

	@GetMapping(value = "/login", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	String login(CsrfToken csrfToken, @RequestParam(required = false) String error,
			@RequestParam(required = false) String logout) {
		var message = "";
		if (error != null) {
			message = ERROR_MESSAGE;
		}
		else if (logout != null) {
			message = LOGOUT_MESSAGE;
		}
		var oauth2Logins = this.clientRegistrations.stream()
			.map(registration -> OAUTH2_LINK.formatted(registration.getRegistrationId(), registration.getClientName()))
			.collect(Collectors.joining());
		return PAGE.formatted(message, csrfToken.getParameterName(), csrfToken.getToken(), oauth2Logins);
	}

}
