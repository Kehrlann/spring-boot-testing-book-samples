package wf.garnier.spring.boot.test.ch7.weather.admin.internal;

import java.util.List;
import java.util.stream.Collectors;

import wf.garnier.spring.boot.test.ch7.weather.city.City;
import wf.garnier.spring.boot.test.ch7.weather.city.CityService;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

/**
 * Lists every user along with the cities they have selected. Access is restricted to
 * admins by the security module.
 * <p>
 * Users are discovered through their selected cities rather than through the security
 * module: OIDC users are not stored anywhere in the application, but their selections
 * are. As a consequence, users who have not selected any city are not listed.
 * <p>
 * Like the login page, this is written out by hand, because the application does not use
 * a template engine. User-provided values must therefore be escaped.
 */
@Controller
class AdminController {

	private static final String PAGE = """
			<!DOCTYPE html>
			<html lang="en">
			<head>
			    <meta charset="UTF-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
			    <title>Weather App - Admin</title>
			    <link href="/css/styles.css" rel="stylesheet">
			</head>
			<body>
			<div class="container">
			    <div class="page-header">
			        <h1>Users and their cities</h1>
			        <a href="/index.html">Back to the app</a>
			    </div>
			%s</div>
			</body>
			</html>
			""";

	private static final String NO_USERS = """
			    <p id="no-users">No user has selected a city yet.</p>
			""";

	private static final String USER = """
			    <div class="card" data-role="user">
			        <h5 class="card-title">%s</h5>
			        <ul>
			%s        </ul>
			    </div>
			""";

	private static final String CITY = """
			            <li>%s (%s)</li>
			""";

	private final CityService cityService;

	AdminController(CityService cityService) {
		this.cityService = cityService;
	}

	@GetMapping(value = "/admin", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	String admin() {
		var citiesByUser = cityService.getSelectedCitiesByUser();
		if (citiesByUser.isEmpty()) {
			return PAGE.formatted(NO_USERS);
		}
		var users = citiesByUser.entrySet()
			.stream()
			.map(entry -> renderUser(entry.getKey(), entry.getValue()))
			.collect(Collectors.joining());
		return PAGE.formatted(users);
	}

	private static String renderUser(UserId user, List<City> cities) {
		var renderedCities = cities.stream()
			.map(city -> CITY.formatted(escape(city.getName()), escape(city.getCountry())))
			.collect(Collectors.joining());
		return USER.formatted(escape(user.value()), renderedCities);
	}

	private static String escape(String value) {
		return HtmlUtils.htmlEscape(value, "UTF-8");
	}

}
