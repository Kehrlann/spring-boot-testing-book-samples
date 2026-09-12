package wf.garnier.spring.boot.test.ch7.weather.preferences;

import org.jspecify.annotations.Nullable;
import wf.garnier.spring.boot.test.ch7.weather.preferences.internal.PreferencesEntity;
import wf.garnier.spring.boot.test.ch7.weather.preferences.internal.PreferencesProperties;
import wf.garnier.spring.boot.test.ch7.weather.preferences.internal.PreferencesRepository;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreferencesService {

	private final PreferencesRepository preferencesRepository;

	private final PreferencesProperties preferencesProperties;

	public PreferencesService(PreferencesRepository preferencesRepository,
			PreferencesProperties preferencesProperties) {
		this.preferencesRepository = preferencesRepository;
		this.preferencesProperties = preferencesProperties;
	}

	public Preferences getPreferences(UserId owner) {
		return getPreferencesEntity(owner);
	}

	private PreferencesEntity getPreferencesEntity(UserId owner) {
		return preferencesRepository.findByOwner(owner).orElseGet(() -> {
			var defaultPrefs = new PreferencesEntity(owner, preferencesProperties.getDefaults().darkMode(),
					preferencesProperties.getDefaults().units(), preferencesProperties.getDefaults().sortBy());
			return preferencesRepository.save(defaultPrefs);
		});
	}

	@Transactional
	public Preferences updatePreferences(UserId owner, @Nullable Boolean darkMode, @Nullable UnitSystem units,
			@Nullable SortOrder sortBy) {
		PreferencesEntity prefs = getPreferencesEntity(owner);
		if (darkMode != null) {
			prefs.setDarkMode(darkMode);
		}
		if (units != null) {
			prefs.setUnits(units);
		}
		if (sortBy != null) {
			prefs.setSortBy(sortBy);
		}
		return preferencesRepository.save(prefs);
	}

}
