package wf.garnier.spring.boot.test.ch1.widget.test.manual;

import java.net.URI;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch1.widget.InvalidWidgetException;
import wf.garnier.spring.boot.test.ch1.widget.WidgetApplication;
import wf.garnier.spring.boot.test.ch1.widget.WidgetRepository;
import wf.garnier.spring.boot.test.ch1.widget.WidgetValidator;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

// tag::content[]
class ManualSpringBootTests {

	// tag::class-members[]
	private static ConfigurableApplicationContext app; // <1>

	private static RestClient restClient; // <2>

	// end::class-members[]
	// tag::before-after[]
	@BeforeAll
	static void beforeAll() {
		var customPropertiesEnvironment = new MockEnvironment().withProperty("server.port", "0")
			.withProperty("widget.id.step", "5");
		app = new SpringApplicationBuilder(TestConfiguration.class).environment(customPropertiesEnvironment).run(); // <1>
		var localServerPort = Integer.parseInt(app.getEnvironment().getProperty("local.server.port"));
		restClient = RestClient.create("http://localhost:" + localServerPort); // <2>
	}

	@AfterAll
	static void afterAll() {
		app.stop(); // <3>
	}

	// end::before-after[]
	// tag::test[]
	// ... boilerplate code ...

	@Test
	void addWidget() { // <3>
		// Given
		StubWidgetValidator validator = (StubWidgetValidator) app.getBean(WidgetValidator.class); // <4>
		validator.makeAlwaysValid();
		var repository = app.getBean(WidgetRepository.class); // <4>

		// When
		var response = restClient.post() // <5>
			.uri("/widget")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body("name=test-widget")
			.retrieve()
			.toBodilessEntity();

		// Then
		var status = response.getStatusCode().value();
		assertThat(status).isEqualTo(HttpStatus.CREATED.value()); // <6>
		var id = getWidgetId(response.getHeaders().getLocation());
		var widget = repository.findById(id);
		assertThat(widget).isPresent();
		assertThat(widget.get().name()).isEqualTo("test-widget"); // <7>
	}

	// end::test[]
	// tag::ignored[]

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

	// end::ignored[]
	// tag::configuration[]
	@EnableAutoConfiguration // <1>
	@ComponentScan(basePackageClasses = WidgetApplication.class, // <2>
			excludeFilters = {
					@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = { SpringBootApplication.class }), // <3>
					@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { WidgetValidator.class }), // <4>
			})
	static class TestConfiguration {

		@Bean
		public WidgetValidator testWidgetValidator() { // <5>
			return new StubWidgetValidator();
		}

	}

	// ... your test code ...

	// end::configuration[]
	// tag::ignored[]
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

	// end::ignored[]
}
// end::content[]
