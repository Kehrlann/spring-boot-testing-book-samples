package wf.garnier.spring.boot.test.ch6.weather.preferences.internal;

import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.dataformat.yaml.YAMLMapper;

import org.springframework.validation.annotation.Validated;
import static org.assertj.core.api.Assertions.assertThat;

class PreferencesPropertiesTests {

	Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	YAMLMapper mapper = YAMLMapper.builder().propertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE).build();

	@Test
	void yamlPrefs() {
		var propString = """
				temperature-threshold:
				  cold: 10
				  hot: 30
				""";
		var props = mapper.readValue(propString, PreferencesProperties.class);

		assertThat(props.getTemperatureThreshold().cold()).isEqualTo(10);
		assertThat(props.getTemperatureThreshold().hot()).isEqualTo(30);
		assertThat(validator.validate(props)).isEmpty();
	}

	@Test
	void objectPrefs() {
		var props = new PreferencesProperties.TemperatureThreshold(-5, 40);

		assertThat(validator.validate(props)).isEmpty();
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
		assertThat(validator.validate(props)).hasSize(2).satisfiesOnlyOnce(violation -> {
			assertThat(violation.getPropertyPath()).hasToString("temperatureThreshold.cold");
			assertThat(violation.getMessage()).isEqualTo("must be greater than or equal to -90");
		}).satisfiesOnlyOnce(violation -> {
			assertThat(violation.getPropertyPath()).hasToString("temperatureThreshold.hot");
			assertThat(violation.getMessage()).isEqualTo("must be less than or equal to 57");
		});
	}

}
