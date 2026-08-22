package wf.garnier.spring.boot.test.ch6.weather.preferences.internal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch6.weather.preferences.PreferencesService;
import wf.garnier.spring.boot.test.ch6.weather.preferences.SortOrder;
import wf.garnier.spring.boot.test.ch6.weather.preferences.UnitSystem;
import wf.garnier.spring.boot.test.ch6.weather.utils.YamlPropertySourceFactory;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.boot.context.properties.bind.validation.ValidationErrors;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.modulith.test.ModuleSlicing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;
import org.springframework.validation.ObjectError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.list;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

class PreferencesConfigurationTests {

	/**
	 * This is a "real"-ish test, where we exercise the public API of the module. The rest
	 * of the tests in this file are more lightweight to showcase the testing
	 */
	@Nested
	// tag::spring-boot-test-properties[]
	@SpringBootTest(properties = """
			preferences.defaults.dark-mode=false
			preferences.defaults.units=imperial
			""") // <1>
	@ModuleSlicing(module = "preferences") // <2>
	class CustomPropertiesValue {

		@Autowired
		PreferencesService service;

		@Test
		void usesCustomDefaults() {
			var prefs = service.getPreferences();
			assertThat(prefs.isDarkMode()).isFalse();
			assertThat(prefs.getUnits()).isEqualTo(UnitSystem.IMPERIAL);
		}

	}
	// end::spring-boot-test-properties[]

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
	// tag::test-property-source[]
	//@formatter:off
	@SpringBootTest(
			classes = PreferencesConfiguration.class, // <1>
			webEnvironment = WebEnvironment.NONE // <2>
	)
	@TestPropertySource(locations = {
			"classpath:units-imperial.properties", // <3>
			"classpath:dark-mode.properties" // <3>
	})
	//@formatter:on
	class FromTestPropertySource {

		@Autowired
		PreferencesProperties props; // <4>

		@Test
		void hasCustomValues() {
			//@formatter:off
			assertThat(props.getDefaults().darkMode()).isTrue();
			assertThat(props.getDefaults().units())
					.isEqualTo(UnitSystem.IMPERIAL);
			//@formatter:on
		}

	}
	// end::test-property-source[]

	@Nested
	// tag::profiles[]
	//@formatter:off
	@SpringBootTest(
			classes = PreferencesConfiguration.class,
			webEnvironment = WebEnvironment.NONE
	)
	//@formatter:on
	@ActiveProfiles({ "dark-mode", "date-added" })
	class ProfileBasedValues {

		// ... tests ...
		// tag::ignored[]
		@Autowired
		PreferencesProperties props;

		@Test
		void hasCustomValues() {
			//@formatter:off
			assertThat(props.getDefaults().darkMode()).isTrue();
			assertThat(props.getDefaults().sortBy())
					.isEqualTo(SortOrder.DATE_ADDED);
			//@formatter:on
		}
		// end::ignored[]

	}
	// end::profiles[]

	@Nested
	@SpringBootTest(classes = PreferencesConfiguration.class, webEnvironment = WebEnvironment.NONE)
	@TestPropertySource
	class FromTestPropertySourceImplicit {

		@Autowired
		PreferencesProperties props;

		// properties are loaded from <test-package>/TestClass.properties
		// wf/garnier/spring/boot/test/ch6/weather/preferences/internal/PreferencesConfigurationTests$FromTestPropertySourceImplicit.properties
		@Test
		void hasCustomValues() {
			assertThat(props.getDefaults().sortBy()).isEqualTo(SortOrder.DATE_ADDED);
		}

	}

	@Nested
	@SpringBootTest(classes = PreferencesConfiguration.class, webEnvironment = WebEnvironment.NONE)
	@TestPropertySources({ @TestPropertySource(locations = "classpath:units-imperial.properties"),
			@TestPropertySource(locations = "classpath:dark-mode.properties") })
	class FromMultipleTestPropertySourceExplicit {

		@Autowired
		PreferencesProperties props;

		@Test
		void hasCustomValues() {
			assertThat(props.getDefaults().units()).isEqualTo(UnitSystem.IMPERIAL);
			assertThat(props.getDefaults().darkMode()).isTrue();
		}

	}

	@Nested
	@SpringBootTest(classes = PreferencesConfiguration.class, webEnvironment = WebEnvironment.NONE)
	@TestPropertySource(locations = "classpath:application-date-added.yaml", factory = YamlPropertySourceFactory.class)
	class FromTestPropertySourceYaml {

		@Autowired
		PreferencesProperties props;

		@Test
		void hasCustomValues() {
			assertThat(props.getDefaults().sortBy()).isEqualTo(SortOrder.DATE_ADDED);
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
	@Disabled // this would fail, the validation is wrong!
	@SpringBootTest(classes = PreferencesConfiguration.class, properties = { """
			preferences.temperature-threshold.cold=30
			preferences.temperature-threshold.hot=5
			""" }, webEnvironment = WebEnvironment.NONE)
	class InvalidValues {

		@Autowired
		PreferencesProperties props;

		@Test
		void hasInvalidValues() {
			assertThat(props.getTemperatureThreshold().cold()).isEqualTo(30);
			assertThat(props.getTemperatureThreshold().hot()).isEqualTo(5);
		}

	}

	@Nested
	class ManualSpringAppConstruction {

		// tag::manual-app[]
		@Test
		void manualAppConstruction() {
			var builder = new SpringApplicationBuilder( // <1>
					PreferencesConfiguration.class // <1>
			).properties("preferences.temperature-threshold.cold=5") // <2>
				.web(WebApplicationType.NONE); // <3>
			try (var applicationContext = builder.run()) { // <4>
				var props = applicationContext // <5>
					.getBean(PreferencesProperties.class); // <5>
				assertThat(props).isNotNull();
				//@formatter:off
				assertThat(props.getTemperatureThreshold().cold())
						.isEqualTo(5);
				//@formatter:on
				// tag::ignored[]
				assertThat(props.getDefaults().darkMode()).isFalse();
				assertThat(props.getDefaults().units()).isEqualTo(UnitSystem.METRIC);
				assertThat(props.getDefaults().sortBy()).isEqualTo(SortOrder.ALPHABETICAL);
				assertThat(props.getTemperatureThreshold().hot()).isEqualTo(25);
				// end::ignored[]
			}
		}
		// end::manual-app[]

		@Test
		void invalidColdThreshold() {
			var builder = new SpringApplicationBuilder(PreferencesConfiguration.class)
				.properties("preferences.temperature-threshold.cold=-100")
				.web(WebApplicationType.NONE);

			//@formatter:off
			assertThatThrownBy(builder::run)
					.rootCause()
					.hasMessageContaining("must be greater than or equal to -90");
			//@formatter:on
		}

		/**
		 * A more complete test than {@link #invalidColdThreshold()}, with details about
		 * the nested exception.
		 */
		@Test
		void invalidColdThresholdFullTest() {
			var builder = new SpringApplicationBuilder(PreferencesConfiguration.class)
				.properties("preferences.temperature-threshold.cold=-100")
				.web(WebApplicationType.NONE);

			assertThatThrownBy(builder::run).isInstanceOf(BeanCreationException.class)
				.rootCause()
				.isInstanceOf(BindValidationException.class)
				.asInstanceOf(type(BindValidationException.class))
				.extracting(BindValidationException::getValidationErrors)
				.extracting(ValidationErrors::getAllErrors)
				.asInstanceOf(list(ObjectError.class))
				.first()
				.extracting(DefaultMessageSourceResolvable::getDefaultMessage)
				.isEqualTo("must be greater than or equal to -90");
		}

		//@formatter:off
		// tag::manual-app-failure[]
		@Test
		void invalidTemperatureRange() {
			var builder = new SpringApplicationBuilder(
					PreferencesConfiguration.class
				).properties(
						"preferences.temperature-threshold.hot=10", // <1>
						"preferences.temperature-threshold.cold=20" // <1>
				)
				.web(WebApplicationType.NONE);

			assertThatThrownBy(builder::run) // <2>
				.isInstanceOf(BeanCreationException.class)
				.rootCause()
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage(
					"preferences.temperature-threshold.hot (10.0) must be " +
					"higher than preferences.temperature-threshold.cold (20.0)"
				);
		}
		// end::manual-app-failure[]
		//@formatter:on

		@Test
		void invalidThresholdRangeFromEnvironment() throws IOException {
			var env = envFromYaml("""
					preferences:
					  temperature-threshold:
					    hot: 10
					    cold: 20
					""");
			var builder = new SpringApplicationBuilder(PreferencesConfiguration.class).web(WebApplicationType.NONE)
				.environment(env);

			assertThatThrownBy(builder::run).isInstanceOf(BeanCreationException.class)
				.rootCause()
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("preferences.temperature-threshold.hot (10.0) " + "must be higher than "
						+ "preferences.temperature-threshold.cold (20.0)");
		}

		@Test
		void invalidThresholdRangeFromYamlProperties() throws IOException {
			var properties = propertiesFromYaml("""
					preferences:
					  temperature-threshold:
					    hot: 10
					    cold: 20
					""");
			var builder = new SpringApplicationBuilder(PreferencesConfiguration.class).web(WebApplicationType.NONE)
				.properties(properties);

			assertThatThrownBy(builder::run).isInstanceOf(BeanCreationException.class)
				.rootCause()
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("preferences.temperature-threshold.hot (10.0) " + "must be higher than "
						+ "preferences.temperature-threshold.cold (20.0)");
		}

		@Test
		void invalidThresholdRangeFromYamlPropertiesFile() {
			var properties = propertiesFromYamlFile("thresholds.yaml");
			var builder = new SpringApplicationBuilder(PreferencesConfiguration.class).web(WebApplicationType.NONE)
				.properties(properties);

			assertThatThrownBy(builder::run).isInstanceOf(BeanCreationException.class)
				.rootCause()
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("preferences.temperature-threshold.hot (10.0) " + "must be higher than "
						+ "preferences.temperature-threshold.cold (20.0)");
		}

		@Test
		void multipleYamlPropertySources() {
			//@formatter:off
			// tag::properties-loaders[]
			var propertiesFromFile = propertiesFromYamlFile("thresholds.yaml");
			var propertiesFromYaml = propertiesFromYaml("""
					preferences:
					  temperature-threshold:
					    cold: 25
					""");

			var builder = new SpringApplicationBuilder(PreferencesConfiguration.class)
				.web(WebApplicationType.NONE)
				.properties(propertiesFromFile)
				.properties(propertiesFromYaml);
			// end::properties-loaders[]
			//@formatter:on

			assertThatThrownBy(builder::run).isInstanceOf(BeanCreationException.class)
				.rootCause()
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("preferences.temperature-threshold.hot (10.0) " + "must be higher than "
						+ "preferences.temperature-threshold.cold (25.0)");
		}

		private static StandardEnvironment envFromYaml(String yamlProperties) throws IOException {
			var config = new ByteArrayResource(yamlProperties.getBytes(StandardCharsets.UTF_8));

			var propertySources = new YamlPropertySourceLoader().load("env-from-inline-test", config);
			var env = new StandardEnvironment();
			env.getPropertySources().addFirst(propertySources.getFirst());
			return env;
		}

		// tag::yaml-utilities[]
		public Properties propertiesFromYaml(String yamlProperties) {
			var yamlBytes = yamlProperties.getBytes(StandardCharsets.UTF_8);
			var resource = new ByteArrayResource(yamlBytes);
			var yamlPropertiesFactory = new YamlPropertiesFactoryBean();
			yamlPropertiesFactory.setResources(resource);
			return yamlPropertiesFactory.getObject();
		}

		public Properties propertiesFromYamlFile(String fileName) {
			var yamlPropertiesFactory = new YamlPropertiesFactoryBean();
			var resource = new ClassPathResource("/" + fileName);
			yamlPropertiesFactory.setResources(resource);
			return yamlPropertiesFactory.getObject();
		}
		// end::yaml-utilities[]

	}

}
