package wf.garnier.spring.boot.test.ch6.weather.preferences;

import org.jspecify.annotations.Nullable;
import wf.garnier.spring.boot.test.ch6.weather.preferences.internal.PreferencesEntity;
import wf.garnier.spring.boot.test.ch6.weather.preferences.internal.PreferencesRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreferencesService {

	private final PreferencesRepository preferencesRepository;

	public PreferencesService(PreferencesRepository preferencesRepository) {
		this.preferencesRepository = preferencesRepository;
	}

	public Preferences getPreferences() {
		return getPreferencesEntity();
	}

	private PreferencesEntity getPreferencesEntity() {
		return preferencesRepository.findAll().stream().findFirst().orElseGet(() -> {
			var defaultPrefs = new PreferencesEntity(false, UnitSystem.METRIC, SortOrder.ALPHABETICAL);
			return preferencesRepository.save(defaultPrefs);
		});
	}

	@Transactional
	public Preferences updatePreferences(@Nullable Boolean darkMode, @Nullable UnitSystem units,
			@Nullable SortOrder sortBy) {
		PreferencesEntity prefs = getPreferencesEntity();
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
