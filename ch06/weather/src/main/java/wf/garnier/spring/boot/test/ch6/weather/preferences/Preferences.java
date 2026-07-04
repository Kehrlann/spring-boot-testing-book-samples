package wf.garnier.spring.boot.test.ch6.weather.preferences;

public interface Preferences {

	Long getId();

	boolean isDarkMode();

	UnitSystem getUnits();

	SortOrder getSortBy();

}
