package wf.garnier.spring.boot.test.ch6.weather.preferences.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import wf.garnier.spring.boot.test.ch6.weather.preferences.SortOrder;
import wf.garnier.spring.boot.test.ch6.weather.preferences.UnitSystem;

@ConfigurationProperties(prefix = "preferences")
public class PreferencesProperties {

	private final Defaults defaults;

	private final Threshold threshold;

	public PreferencesProperties(@DefaultValue Defaults defaults, @DefaultValue Threshold threshold) {
		this.defaults = defaults;
		this.threshold = threshold;
	}

	public Defaults getDefaults() {
		return defaults;
	}

	public Threshold getThreshold() {
		return threshold;
	}

	public record Defaults(@DefaultValue("false") boolean darkMode, @DefaultValue("metric") UnitSystem units,
			@DefaultValue("alphabetical") SortOrder sortBy) {
	}

	public record Threshold(@DefaultValue("5") double cold, @DefaultValue("30") double hot) {
	}

}
