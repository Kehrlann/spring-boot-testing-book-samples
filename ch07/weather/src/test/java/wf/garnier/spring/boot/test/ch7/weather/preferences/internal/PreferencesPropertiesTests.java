package wf.garnier.spring.boot.test.ch7.weather.preferences.internal;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch7.weather.preferences.internal.PreferencesProperties.TemperatureThreshold;

import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import static org.assertj.core.api.Assertions.assertThat;

class PreferencesPropertiesTests {

	LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();

	@BeforeEach
	void setUp() {
		validator.afterPropertiesSet();
	}

	@Test
	void constraintValidation() {
		var valid = new TemperatureThreshold(-5, 40);
		assertThat(validator.validate(valid)).isEmpty();

		var invalid = new TemperatureThreshold(-100, 40);
		Set<ConstraintViolation<TemperatureThreshold>> violations = validator.validate(invalid);

		assertThat(violations).hasSize(1).first().satisfies(violation -> {
			assertThat(violation.getPropertyPath()).hasToString("cold");
			assertThat(violation.getMessage()).isEqualTo("must be greater than or equal to -90");
		});
	}

}
