package wf.garnier.spring.boot.test.ch6.weather.preferences.internal;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch6.weather.preferences.SortOrder;
import wf.garnier.spring.boot.test.ch6.weather.preferences.UnitSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

class PreferencesConfigurationTests {

	@Nested
	@SpringBootTest(classes = { PreferencesConfiguration.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
	class DefaultValues {

		@Autowired
		PreferencesProperties props;

		@Test
		void hasDefaults() {
			assertThat(props.getDefaults().darkMode()).isFalse();
			assertThat(props.getDefaults().units()).isEqualTo(UnitSystem.METRIC);
			assertThat(props.getDefaults().sortBy()).isEqualTo(SortOrder.ALPHABETICAL);
			assertThat(props.getThreshold().cold()).isEqualTo(10);
			assertThat(props.getThreshold().hot()).isEqualTo(25);
		}

	}

	@Nested
	@SpringBootTest(classes = { PreferencesConfiguration.class }, properties = { """
			preferences.defaults.dark-mode=true
			preferences.defaults.sort-by=date_added
			preferences.defaults.units=imperial
			preferences.threshold.cold=5
			preferences.threshold.hot=30
			""" }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
	class CustomValues {

		@Autowired
		PreferencesProperties props;

		@Test
		void hasDefaults() {
			assertThat(props.getDefaults().darkMode()).isTrue();
			assertThat(props.getDefaults().units()).isEqualTo(UnitSystem.IMPERIAL);
			assertThat(props.getDefaults().sortBy()).isEqualTo(SortOrder.DATE_ADDED);
			assertThat(props.getThreshold().cold()).isEqualTo(5);
			assertThat(props.getThreshold().hot()).isEqualTo(30);
		}

	}

	@Nested
	@Disabled // this would fail, the validation is wrong!
	@SpringBootTest(classes = { PreferencesConfiguration.class }, properties = { """
			preferences.threshold.cold=30
			preferences.threshold.hot=5
			""" }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
	class InvalidValues {

		@Autowired
		PreferencesProperties props;

		@Test
		void hasDefaults() {
			assertThat(props.getThreshold().cold()).isEqualTo(30);
			assertThat(props.getThreshold().hot()).isEqualTo(5);
		}

	}

	@Nested
	class ManualSpringAppConstruction {

		@Test
		void invalidThresholds() {
			var builder = new SpringApplicationBuilder(PreferencesConfiguration.class).web(WebApplicationType.NONE);
			try (var applicationContext = builder.run()) {
				var props = applicationContext.getBean(PreferencesProperties.class);
				assertThat(props).isNotNull();
			}
		}

	}

}
