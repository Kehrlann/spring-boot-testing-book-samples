package wf.garnier.spring.boot.test.ch5.weather.preferences;

public interface Preferences {

	Long getId();

	boolean isDarkMode();

	UnitSystem getUnits();

	SortOrder getSortBy();

}
