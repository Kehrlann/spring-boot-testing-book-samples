package wf.garnier.spring.boot.test.ch7.weather.preferences.internal;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import wf.garnier.spring.boot.test.ch7.weather.preferences.Preferences;
import wf.garnier.spring.boot.test.ch7.weather.preferences.SortOrder;
import wf.garnier.spring.boot.test.ch7.weather.preferences.UnitSystem;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;

@Entity
@Table(name = "preferences")
public class PreferencesEntity implements Preferences {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "user_id", nullable = false))
	private UserId owner;

	private boolean darkMode;

	@Enumerated(EnumType.STRING)
	private UnitSystem units;

	@Enumerated(EnumType.STRING)
	private SortOrder sortBy;

	public PreferencesEntity() {
	}

	public PreferencesEntity(UserId owner, boolean darkMode, UnitSystem units, SortOrder sortBy) {
		this.owner = owner;
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

	public UserId getOwner() {
		return owner;
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
