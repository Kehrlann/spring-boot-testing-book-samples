package wf.garnier.spring.boot.test.ch5.weather.preferences.internal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import wf.garnier.spring.boot.test.ch5.weather.preferences.Preferences;
import wf.garnier.spring.boot.test.ch5.weather.preferences.SortOrder;
import wf.garnier.spring.boot.test.ch5.weather.preferences.UnitSystem;

@Entity
@Table(name = "preferences")
public class PreferencesEntity implements Preferences {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private boolean darkMode;

	@Enumerated(EnumType.STRING)
	private UnitSystem units;

	@Enumerated(EnumType.STRING)
	private SortOrder sortBy;

	public PreferencesEntity() {
	}

	public PreferencesEntity(boolean darkMode, UnitSystem units, SortOrder sortBy) {
		this.darkMode = darkMode;
		this.units = units;
		this.sortBy = sortBy;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean isDarkMode() {
		return darkMode;
	}

	public void setDarkMode(boolean darkMode) {
		this.darkMode = darkMode;
	}

	@Override
	public UnitSystem getUnits() {
		return units;
	}

	public void setUnits(UnitSystem units) {
		this.units = units;
	}

	@Override
	public SortOrder getSortBy() {
		return sortBy;
	}

	public void setSortBy(SortOrder sortBy) {
		this.sortBy = sortBy;
	}

}
