package wf.garnier.spring.boot.test.ch1.widget.test.manual;

import java.net.URI;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch1.widget.InvalidWidgetException;
import wf.garnier.spring.boot.test.ch1.widget.WidgetApplication;
import wf.garnier.spring.boot.test.ch1.widget.WidgetRepository;
import wf.garnier.spring.boot.test.ch1.widget.WidgetValidator;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * The examples in this class are not in the book, they apply more complicated than what
 * you'll find in {@link ManualSpringBootTests}. They use Spring-specific tricks to make
 * the configuration simpler ... or at least different :)
 * <p>
 * Same advice as the other manual tests: You should NOT write these kinds of tests in
 * your real projects, and instead draw inspiration from
 * {@link wf.garnier.spring.boot.test.ch1.widget.test.boot.AwesomeSpringBootTests}.
 */
class SimpleManualSpringBootTests {

	private static ConfigurableApplicationContext app;

	private static RestClient restClient;

	private static int localServerPort;

	@BeforeAll
	static void beforeAll() {
		// Instead of using a @PropertySource, we can inject our own version of the Spring
		// "Environment" class, which manages profiles and properties.
		// See the reference docs for Environment:
		// https://docs.spring.io/spring-framework/reference/core/beans/environment.html#page-title
		// See the reference docs for MockEnvironment:
		// https://docs.spring.io/spring-framework/reference/testing/unit.html#mock-objects-env
		var customPropertiesEnvironment = new MockEnvironment().withProperty("server.port", "0")
			.withProperty("widget.id.step", "5");
		app = new SpringApplicationBuilder(TestConfiguration.class, WidgetApplication.class)
			.environment(customPropertiesEnvironment)
			.run();
		localServerPort = Integer.parseInt(app.getEnvironment().getProperty("local.server.port"));
		restClient = RestClient.create("http://localhost:" + localServerPort);
	}

	@AfterAll
	static void afterAll() {
		app.stop();
	}

	@Test
	void addWidget() {
		// Given
		StubWidgetValidator validator = (StubWidgetValidator) app.getBean(WidgetValidator.class);
		validator.makeAlwaysValid();
		var repository = app.getBean(WidgetRepository.class);

		// When
		var response = restClient.post()
			.uri("/widget")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body("name=test-widget")
			.retrieve()
			.toBodilessEntity();

		// Then
		var status = response.getStatusCode().value();
		assertThat(status).isEqualTo(HttpStatus.CREATED.value());
		var id = getWidgetId(response.getHeaders().getLocation());
		var widget = repository.findById(id);
		assertThat(widget).isPresent();
		assertThat(widget.get().name()).isEqualTo("test-widget");
	}

	// Some tests that do not show up in the examples
	@Test
	void runsOnRandomPort() {
		assertThat(localServerPort).isNotEqualTo(8080);
	}

	@Test
	void addWidgetRejected() {
		StubWidgetValidator validator = (StubWidgetValidator) app.getBean(WidgetValidator.class);
		validator.makeAlwaysInvalid();
		var repository = app.getBean(WidgetRepository.class);
		var currentCount = repository.count();

		assertThatExceptionOfType(HttpClientErrorException.BadRequest.class).isThrownBy(() -> restClient.post()
			.uri("/widget")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body("name=test-widget")
			.retrieve()
			.toBodilessEntity());

		assertThat(repository.count()).isEqualTo(currentCount);
	}

	@Test
	void widgetIdIncrementsWithStep() {
		StubWidgetValidator validator = (StubWidgetValidator) app.getBean(WidgetValidator.class);
		validator.makeAlwaysValid();

		var firstWidgetResponse = restClient.post()
			.uri("/widget")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body("name=test-widget")
			.retrieve()
			.toBodilessEntity();
		var firstId = getWidgetId(firstWidgetResponse.getHeaders().getLocation());
		var secondWidgetResponse = restClient.post()
			.uri("/widget")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body("name=test-widget")
			.retrieve()
			.toBodilessEntity();
		var secondId = getWidgetId(secondWidgetResponse.getHeaders().getLocation());

		assertThat(secondId - firstId).isEqualTo(5);
	}

	static class TestConfiguration {

		@Primary
		@Bean
		public WidgetValidator simpleTestWidgetValidator() {
			return new StubWidgetValidator();
		}

	}

	private static int getWidgetId(URI location) {
		var id = UriComponentsBuilder.fromUri(location)
				.build()
				.getPathSegments()
				.getLast();
		return Integer.parseInt(id);
	}

	private static class StubWidgetValidator extends WidgetValidator {

		private boolean valid = true;

		public StubWidgetValidator makeAlwaysValid() {
			this.valid = true;
			return this;
		}

		public StubWidgetValidator makeAlwaysInvalid() {
			this.valid = false;
			return this;
		}

		@Override
		public void validateWidget(String name) throws InvalidWidgetException {
			if (!valid) {
				throw new InvalidWidgetException("Invalid widget, for some random reason");
			}
		}

	}

}
