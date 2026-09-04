package wf.garnier.spring.boot.test.ch7.weather.preferences.internal;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.dataformat.yaml.YAMLMapper;
import wf.garnier.spring.boot.test.ch7.weather.preferences.internal.PreferencesProperties.TemperatureThreshold;

import org.springframework.context.MessageSource;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import static org.assertj.core.api.Assertions.assertThat;

// tag::class[]
class PreferencesPropertiesTests {

	// end::class[]
	//@formatter:off
	// tag::mapper[]
	YAMLMapper mapper = YAMLMapper.builder()
			.propertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)
			.build();

	// end::mapper[]
	//@formatter:on
	// tag::validator[]
	LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean(); // <1>

	@BeforeEach
	void setUp() {
		validator.afterPropertiesSet(); // <1>
	}

	// end::validator[]
	//@formatter:off
	// tag::constraint-validation[]
	@Test
	void constraintValidation() {
		var valid = new TemperatureThreshold(-5, 40); // <2>
		assertThat(validator.validate(valid)).isEmpty(); // <2>

		var invalid = new TemperatureThreshold(-100, 40); // <3>
		Set<ConstraintViolation<TemperatureThreshold>> violations = // <3>
				validator.validate(invalid); // <3>

		assertThat(violations).hasSize(1)
				.first()
				.satisfies(violation -> {
					assertThat(violation.getPropertyPath()) // <4>
							.hasToString("cold");
					assertThat(violation.getMessage()) // <4>
							.isEqualTo("must be greater than or equal to -90");
				});
	}
	// end::constraint-validation[]
	//@formatter:on

	@Test
	void fullPropertiesObject() {
		//@formatter:off
		// tag::full-properties-object[]
		var invalid = new PreferencesProperties(
				null,
				new TemperatureThreshold(-100, 40)
		);
		// end::full-properties-object[]
		assertThat(validator.validate(invalid)).hasSize(1)
				.first()
				.satisfies(violation -> {
					assertThat(violation.getPropertyPath())
							.hasToString("temperatureThreshold.cold");
					assertThat(violation.getMessage())
							.isEqualTo("must be greater than or equal to -90");
				});
	}

	//tag::yaml-preferences[]
	@Test
	void yamlPreferences() {
		var propString = """
				temperature-threshold:
				  cold: 10
				  hot: 30
				""";
		var props = mapper.readValue(propString, PreferencesProperties.class);

		assertThat(validator.validate(props)).isEmpty();
		// tag::ignored[]
		assertThat(props.getTemperatureThreshold().cold()).isEqualTo(10);
		assertThat(props.getTemperatureThreshold().hot()).isEqualTo(30);
		// end::ignored[]
	}
	//end::yaml-preferences[]

	/**
	 * Same as {@link #constraintValidation()} but using the raw Jakarta validator. It
	 * does not validate {@link Validated} annotations, and does not support i18n with
	 * {@link MessageSource} like the Spring {@link LocalValidatorFactoryBean} does.
	 *
	 * <p>
	 * The wiring is much simpler, as it is a single call to static method.
	 */
	@Test
	void alternativeValidation() {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

		var invalid = new PreferencesProperties.TemperatureThreshold(-100, 40);
		var violations = validator.validate(invalid);

		assertThat(violations).hasSize(1);
	}

	/**
	 * These tests assert that the full validation of {@link PreferencesProperties} is
	 * correct. They would fail if {@link Valid} or {@link Validated} were missing.
	 */
	@Test
	void thresholdViolations() {
		var propString = """
				temperature-threshold:
				  cold: -100
				  hot: 100
				""";
		var props = mapper.readValue(propString, PreferencesProperties.class);

		assertThat(props.getTemperatureThreshold().cold()).isEqualTo(-100);
		assertThat(props.getTemperatureThreshold().hot()).isEqualTo(100);
		//@formatter:off
		assertThat(validator.validate(props))
			.hasSize(2)
			.satisfiesOnlyOnce(violation -> {
				assertThat(violation.getPropertyPath())
						.hasToString("temperatureThreshold.cold");
				assertThat(violation.getMessage())
						.isEqualTo("must be greater than or equal to -90");
			})
			.satisfiesOnlyOnce(violation -> {
				assertThat(violation.getPropertyPath())
						.hasToString("temperatureThreshold.hot");
				assertThat(violation.getMessage())
						.isEqualTo("must be less than or equal to 57");
			});
		//@formatter:on
	}
	// tag::class[]

}
// end::class[]