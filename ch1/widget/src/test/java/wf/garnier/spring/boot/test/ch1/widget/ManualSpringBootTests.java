package wf.garnier.spring.boot.test.ch1.widget;

import java.net.URI;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import static org.assertj.core.api.Assertions.assertThat;

// tag::class-definition[]
class ManualSpringBootTests {

	private static ConfigurableApplicationContext app;

	private static RestClient restClient;

	// end::class-definition[]

	// tag::before-after[]
	@BeforeAll
	static void beforeAll() {
		app = new SpringApplication(TestConfiguration.class).run();
		var localServerPort = Integer.parseInt(app.getEnvironment().getProperty("local.server.port"));
		restClient = RestClient.create("http://localhost:" + localServerPort);
	}

	@AfterAll
	static void afterAll() {
		app.stop();
	}
	// end::before-after[]

	// tag::test[]
	@Test
	void addWidget() {
		StubWidgetValidator validator = (StubWidgetValidator) app.getBean(WidgetValidator.class);
		validator.makeAlwaysValid();
		var repository = app.getBean(WidgetRepository.class);

		var response = restClient.post()
			.uri("/widget")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body("name=test-widget")
			.retrieve()
			.toBodilessEntity();

		assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.CREATED.value());
		var id = getWidgetId(response.getHeaders().getLocation());
		var widget = repository.findById(id);
		assertThat(widget).isPresent();
		assertThat(widget.get().name()).isEqualTo("test-widget");
	}
	// end::test[]

	private static int getWidgetId(URI location) {
		//@formatter:off
        var id = UriComponentsBuilder.fromUri(location)
                .build()
                .getPathSegments()
                .getLast();
        //@formatter:on
		return Integer.parseInt(id);
	}

	// tag::configuration[]
	@Configuration
	@EnableAutoConfiguration
	@ComponentScan(basePackageClasses = ManualSpringBootTests.class,
			excludeFilters = {
					@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { WidgetValidator.class }),
					@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { WidgetApplication.class }) })
	@PropertySource("test.properties")
	static class TestConfiguration {

		@Bean
		public WidgetValidator testWidgetValidator() {
			return new StubWidgetValidator();
		}

	}
	// end::configuration[]

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
