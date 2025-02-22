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

class SimpleManualSpringBootTests {

	private static ConfigurableApplicationContext app;

	private static RestClient restClient;

	@BeforeAll
	static void beforeAll() {
		var customPropertiesEnvironment = new MockEnvironment().withProperty("server.port", "0")
			.withProperty("widget.id.step", "5");
		app = new SpringApplicationBuilder(TestConfiguration.class, WidgetApplication.class)
			.environment(customPropertiesEnvironment)
			.run();
		var localServerPort = Integer.parseInt(app.getEnvironment().getProperty("local.server.port"));
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
		//@formatter:off
		var id = UriComponentsBuilder.fromUri(location)
				.build()
				.getPathSegments()
				.getLast();
		//@formatter:on
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
