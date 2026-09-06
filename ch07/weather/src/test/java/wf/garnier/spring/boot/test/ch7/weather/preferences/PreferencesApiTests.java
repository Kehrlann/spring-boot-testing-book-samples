package wf.garnier.spring.boot.test.ch7.weather.preferences;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch7.weather.preferences.internal.PreferencesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@ApplicationModuleTest
@AutoConfigureMockMvc
@WithMockUser
class PreferencesApiTests {

	@Autowired
	MockMvcTester mvc;

	@Autowired
	private PreferencesRepository preferencesRepository;

	@BeforeEach
	void setUp() {
		preferencesRepository.deleteAll();
	}

	@Test
	void defaultPreferences() {
		var response = mvc.get().uri("/api/preferences").exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
				{
				  "darkMode": false,
				  "units": "METRIC",
				  "sortBy": "ALPHABETICAL"
				}
				""");
	}

	@Test
	void updatePreferences() {
		var response = mvc.put()
			.uri("/api/preferences")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
					{
					  "darkMode": true,
					  "sortBy": "DATE_ADDED"
					}
					""")
			.exchange();

		assertThat(response).hasStatus(HttpStatus.OK).bodyJson().isLenientlyEqualTo("""
				{
				  "darkMode": true,
				  "units": "METRIC",
				  "sortBy": "DATE_ADDED"
				}
				""");
	}

}
