package wf.garnier.spring.boot.test.ch7.weather.preferences.internal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch7.weather.preferences.SortOrder;
import wf.garnier.spring.boot.test.ch7.weather.preferences.UnitSystem;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

class PreferencesConfigurationTests {

	@Nested
	@SpringBootTest(classes = PreferencesConfiguration.class, webEnvironment = WebEnvironment.NONE)
	class DefaultValues {

		@Autowired
		PreferencesProperties props;

		@Test
		void hasDefaults() {
			assertThat(props.getDefaults().darkMode()).isFalse();
			assertThat(props.getDefaults().units()).isEqualTo(UnitSystem.METRIC);
			assertThat(props.getDefaults().sortBy()).isEqualTo(SortOrder.ALPHABETICAL);
			assertThat(props.getTemperatureThreshold().cold()).isEqualTo(10);
			assertThat(props.getTemperatureThreshold().hot()).isEqualTo(25);
		}

	}

	@Nested
	@SpringBootTest(classes = PreferencesConfiguration.class, properties = { """
			preferences.defaults.dark-mode=true
			preferences.defaults.sort-by=date_added
			preferences.defaults.units=imperial
			preferences.temperature-threshold.cold=5
			preferences.temperature-threshold.hot=30
			""" }, webEnvironment = WebEnvironment.NONE)
	class CustomValues {

		@Autowired
		PreferencesProperties props;

		@Test
		void hasCustomValues() {
			assertThat(props.getDefaults().darkMode()).isTrue();
			assertThat(props.getDefaults().units()).isEqualTo(UnitSystem.IMPERIAL);
			assertThat(props.getDefaults().sortBy()).isEqualTo(SortOrder.DATE_ADDED);
			assertThat(props.getTemperatureThreshold().cold()).isEqualTo(5);
			assertThat(props.getTemperatureThreshold().hot()).isEqualTo(30);
		}

	}

	@Nested
	class InvalidConfiguration {

		@Test
		void invalidColdThreshold() {
			var builder = new SpringApplicationBuilder(PreferencesConfiguration.class)
				.properties("preferences.temperature-threshold.cold=-100")
				.web(WebApplicationType.NONE);

			assertThatThrownBy(builder::run).rootCause().hasMessageContaining("must be greater than or equal to -90");
		}

		@Test
		void invalidTemperatureRange() {
			var builder = new SpringApplicationBuilder(PreferencesConfiguration.class)
				.properties("preferences.temperature-threshold.hot=10", "preferences.temperature-threshold.cold=20")
				.web(WebApplicationType.NONE);

			assertThatThrownBy(builder::run).isInstanceOf(BeanCreationException.class)
				.rootCause()
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("preferences.temperature-threshold.hot (10.0) must be "
						+ "higher than preferences.temperature-threshold.cold (20.0)");
		}

	}

}
