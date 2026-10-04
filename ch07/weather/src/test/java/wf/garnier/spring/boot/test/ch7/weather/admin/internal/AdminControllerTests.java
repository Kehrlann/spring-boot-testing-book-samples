package wf.garnier.spring.boot.test.ch7.weather.admin.internal;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.htmlunit.WebClient;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch7.weather.city.City;
import wf.garnier.spring.boot.test.ch7.weather.city.CityService;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.modulith.test.ModuleSlicing;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * The security module is included so that the access rules for {@code /admin} apply.
 */
@ModuleSlicing(extraIncludes = "security")
@WebMvcTest
@WithMockUser(roles = "ADMIN")
class AdminControllerTests {

	@MockitoBean
	CityService cityService;

	@Autowired
	MockMvcTester mvc;

	@Autowired
	WebClient webClient;

	@Test
	void listsUsersAndTheirCities() throws IOException {
		var citiesByUser = new LinkedHashMap<UserId, List<City>>();
		citiesByUser.put(new UserId("alice"), List.of(city("Paris", "France"), city("Tokyo", "Japan")));
		citiesByUser.put(new UserId("bob"), List.of(city("Xi’an", "China")));
		when(cityService.getSelectedCitiesByUser()).thenReturn(citiesByUser);

		HtmlPage page = webClient.getPage("/admin");

		var users = page.querySelectorAll("[data-role=user]");
		assertThat(users).map(user -> user.querySelector(".card-title").getTextContent())
			.containsExactly("alice", "bob");
		assertThat(users.get(0).querySelectorAll("li")).map(DomNode::getTextContent)
			.containsExactly("Paris (France)", "Tokyo (Japan)");
		assertThat(users.get(1).querySelectorAll("li")).map(DomNode::getTextContent).containsExactly("Xi’an (China)");
		assertThat(page.getElementById("no-users")).isNull();
	}

	@Test
	void noUsers() throws IOException {
		when(cityService.getSelectedCitiesByUser()).thenReturn(Map.of());

		HtmlPage page = webClient.getPage("/admin");

		assertThat(page.querySelectorAll("[data-role=user]")).isEmpty();
		assertThat(page.getElementById("no-users").getTextContent()).isEqualTo("No user has selected a city yet.");
	}

	@Test
	void escapesValues() {
		when(cityService.getSelectedCitiesByUser())
			.thenReturn(Map.of(new UserId("<script>alert('pwned')</script>"), List.of(city("<b>Paris</b>", "France"))));

		var response = mvc.get().uri("/admin").exchange();

		assertThat(response).hasStatus(HttpStatus.OK)
			.bodyText()
			.contains("&lt;script&gt;alert(&#39;pwned&#39;)&lt;/script&gt;")
			.contains("&lt;b&gt;Paris&lt;/b&gt; (France)")
			.doesNotContain("<script>", "<b>");
	}

	@Test
	@WithMockUser
	void forbiddenForNonAdmins() {
		var response = mvc.get().uri("/admin").exchange();

		assertThat(response).hasStatus(HttpStatus.FORBIDDEN);
	}

	@Test
	@WithAnonymousUser
	void requiresAuthentication() {
		var response = mvc.get().uri("/admin").exchange();

		assertThat(response).hasStatus(HttpStatus.FOUND).hasRedirectedUrl("/login");
	}

	private static City city(String name, String country) {
		return new TestCity(name, country);
	}

	record TestCity(String name, String country) implements City {

		@Override
		public Integer getId() {
			return 1;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getCountry() {
			return country;
		}

		@Override
		public double getLatitude() {
			return 0;
		}

		@Override
		public double getLongitude() {
			return 0;
		}

	}

}
