package wf.garnier.spring.boot.test.ch6.weather.weather.internal;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class WeatherServicePropertiesTests {

	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@ParameterizedTest
	@ValueSource(strings = { "http://example.com", "HTTP://example.com", "httpS://example.com", "https://example.com" })
	void validUrl(String validUrl) {
		var violations = validator.validate(new WeatherServiceProperties(validUrl));

		assertThat(violations).isEmpty();
	}

	@ParameterizedTest
	@ValueSource(strings = { "foobar", "ftp://example.com", "", "example.com", "www.example.com", "http://",
			"http:/example.com", " http://example.com", "http://example.com ", "mailto:foo@example.com",
			"javascript:alert(1)", "file:///etc/passwd", "http:example.com", "//example.com" })
	void invalidUrl(String invalidUrl) {
		var violations = validator.validate(new WeatherServiceProperties(invalidUrl));

		assertThat(violations).hasSize(1)
			.first()
			.extracting(ConstraintViolation::getMessage)
			.isEqualTo("must be a valid URL");
	}

}
