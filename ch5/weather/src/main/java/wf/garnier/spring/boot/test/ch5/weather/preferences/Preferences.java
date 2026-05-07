package wf.garnier.spring.boot.test.ch5.weather.preferences;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Preferences {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private boolean darkMode;

	@Enumerated(EnumType.STRING)
	private UnitSystem units;

	@Enumerated(EnumType.STRING)
	private SortOrder sortBy;

	public Preferences() {
	}

	public Preferences(boolean darkMode, UnitSystem units, SortOrder sortBy) {
		this.darkMode = darkMode;
		this.units = units;
		this.sortBy = sortBy;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isDarkMode() {
		return darkMode;
	}

	public void setDarkMode(boolean darkMode) {
		this.darkMode = darkMode;
	}

	public UnitSystem getUnits() {
		return units;
	}

	public void setUnits(UnitSystem units) {
		this.units = units;
	}

	public SortOrder getSortBy() {
		return sortBy;
	}

	public void setSortBy(SortOrder sortBy) {
		this.sortBy = sortBy;
	}

}
