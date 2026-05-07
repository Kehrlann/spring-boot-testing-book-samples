package wf.garnier.spring.boot.test.ch5.weather.preferences;

import org.jspecify.annotations.Nullable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreferencesService {

	private final PreferencesRepository preferencesRepository;

	public PreferencesService(PreferencesRepository preferencesRepository) {
		this.preferencesRepository = preferencesRepository;
	}

	public Preferences getPreferences() {
		return preferencesRepository.findAll().stream().findFirst().orElseGet(() -> {
			Preferences defaultPrefs = new Preferences(false, UnitSystem.METRIC, SortOrder.ALPHABETICAL);
			return preferencesRepository.save(defaultPrefs);
		});
	}

	@Transactional
	public Preferences updatePreferences(@Nullable Boolean darkMode, @Nullable UnitSystem units,
			@Nullable SortOrder sortBy) {
		Preferences prefs = getPreferences();
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
