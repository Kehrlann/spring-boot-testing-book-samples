package wf.garnier.spring.boot.test.ch6.weather.preferences.internal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import wf.garnier.spring.boot.test.ch6.weather.preferences.SortOrder;
import wf.garnier.spring.boot.test.ch6.weather.preferences.UnitSystem;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "preferences")
public class PreferencesProperties implements InitializingBean {

	private final @Valid Defaults defaults;

	private final @Valid TemperatureThreshold temperatureThreshold;

	public PreferencesProperties(@DefaultValue Defaults defaults,
			@DefaultValue TemperatureThreshold temperatureThreshold) {
		this.defaults = defaults;
		this.temperatureThreshold = temperatureThreshold;
	}

	public Defaults getDefaults() {
		return defaults;
	}

	public TemperatureThreshold getTemperatureThreshold() {
		return temperatureThreshold;
	}

	public record Defaults(@DefaultValue("false") boolean darkMode, @DefaultValue("metric") UnitSystem units,
			@DefaultValue("alphabetical") SortOrder sortBy) {
	}

	/**
	 * @param cold temperature below which the UI will display temperatures in blue.
	 * Minimum is -90˚C, see
	 * <a href="https://en.wikipedia.org/wiki/Lowest_temperature_recorded_on_Earth">Lowest
	 * temperature recorded on Earth</a>.
	 * @param hot temperature below which the UI will display temperatures in blue.
	 * Minimum is +57˚C, see <a href=
	 * "https://en.wikipedia.org/wiki/Highest_temperature_recorded_on_Earth">Lowest *
	 * temperature recorded on Earth</a>.
	 */
	public record TemperatureThreshold(@Min(-90) @DefaultValue("10") double cold,
			@Max(57) @DefaultValue("25") double hot) {
	}

	@Override
	public void afterPropertiesSet() {
		if (temperatureThreshold.cold() >= temperatureThreshold.hot()) {
			throw new IllegalArgumentException(
					"preferences.temperature-threshold.hot (%s) must be higher than preferences.temperature-threshold.cold (%s)"
						.formatted(temperatureThreshold.hot(), temperatureThreshold.cold()));
		}
	}

}
