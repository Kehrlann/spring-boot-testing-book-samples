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
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * This test showcases everything you'd need to do in a "general case" if you did not use
 * {@link org.springframework.boot.test.context.SpringBootTest}, including building a very
 * custom Spring configuration. You should NOT write those kind of tests in your real
 * projects, and instead draw inspiration from
 * {@link wf.garnier.spring.boot.test.ch1.widget.test.boot.AwesomeSpringBootTests}.
 * <p>
 * You can use some context-specific tricks to achieve the same results, without the
 * complex {@link TestConfiguration} class we use here, but it goes beyond the scope of
 * the book. If you are curious, please take a look at
 * {@link SimpleManualSpringBootTests}.
 */
// tag::content[]
class ManualSpringBootTests {

	// tag::class-members[]
	private static ConfigurableApplicationContext app; // <1>

	private static RestClient restClient; // <2>

	private static int localServerPort;

	// end::class-members[]
	// tag::before-after[]
	@BeforeAll
	static void beforeAll() {
		app = new SpringApplicationBuilder(TestConfiguration.class).run(); // <1>
		localServerPort = Integer.parseInt(app.getEnvironment().getProperty("local.server.port"));
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
		assertThat(response.getHeaders().getLocation().getPath()).matches("^/widget/\\d+$");
		var id = getWidgetId(response.getHeaders().getLocation());
		var widget = repository.findById(id);
		assertThat(widget).isPresent();
		assertThat(widget.get().name()).isEqualTo("test-widget"); // <7>
	}

	// end::test[]
	// tag::ignored[]

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

	@Test
	void addWidgetRestClient() {
		StubWidgetValidator validator = (StubWidgetValidator) app.getBean(WidgetValidator.class);
		validator.makeAlwaysValid();

		// tag::restclient[]
		var body = new LinkedMultiValueMap<String, String>();
		body.add("name", "test-widget");

		var response = restClient.post()
			.uri("/widget")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(body)
			.retrieve()
			.toEntity(String.class);
		// end::restclient[]

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	/**
	 * Here's an example using the older
	 * {@link org.springframework.web.client.RestTemplate} API, which you might find in
	 * legacy applications.
	 */
	@Test
	void addWidgetRestTemplate() {
		// Given
		StubWidgetValidator validator = (StubWidgetValidator) app.getBean(WidgetValidator.class);
		validator.makeAlwaysValid();
		var repository = app.getBean(WidgetRepository.class);

		// When
		var restTemplate = app.getBean(RestTemplateBuilder.class)
			.rootUri("http://localhost:" + localServerPort)
			.build();

		// tag::resttemplate[]
		var body = new LinkedMultiValueMap<String, String>();
		body.add("name", "test-widget");
		var request = RequestEntity.post("/widget").contentType(MediaType.APPLICATION_FORM_URLENCODED).body(body);
		var response = restTemplate.exchange(request, String.class);
		// end::resttemplate[]

		var status = response.getStatusCode().value();
		assertThat(status).isEqualTo(HttpStatus.CREATED.value());
		var locationHeader = response.getHeaders().getLocation().getPath();
		assertThat(locationHeader).matches("^/widget/\\d+$");

		var id = getWidgetId(response.getHeaders().getLocation());
		var widget = repository.findById(id);
		assertThat(widget).isPresent();
		assertThat(widget.get().name()).isEqualTo("test-widget"); // <7>
	}

	// end::ignored[]
	// tag::configuration[]
	@EnableAutoConfiguration
	@ComponentScan(basePackageClasses = WidgetApplication.class,
			excludeFilters = {
					@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = { SpringBootApplication.class }),
					@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { WidgetValidator.class }), })
	@PropertySource("classpath:test.properties")
	static class TestConfiguration {

		@Bean
		public WidgetValidator testWidgetValidator() {
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
