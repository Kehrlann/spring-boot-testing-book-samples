package wf.garnier.spring.boot.test.ch6.weather.preferences.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import wf.garnier.spring.boot.test.ch6.weather.preferences.SortOrder;
import wf.garnier.spring.boot.test.ch6.weather.preferences.UnitSystem;

@ConfigurationProperties(prefix = "preferences.defaults")
public class PreferencesProperties {

	private final boolean darkMode;

	private final UnitSystem units;

	private final SortOrder sortBy;

	public PreferencesProperties(@DefaultValue("false") boolean darkMode, @DefaultValue("metric") UnitSystem units,
			@DefaultValue("alphabetical") SortOrder sortBy) {
		this.darkMode = darkMode;
		this.units = units;
		this.sortBy = sortBy;
	}

	public boolean isDarkMode() {
		return darkMode;
	}

	public UnitSystem getUnits() {
		return units;
	}

	public SortOrder getSortBy() {
		return sortBy;
	}

}
