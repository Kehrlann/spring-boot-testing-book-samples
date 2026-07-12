package wf.garnier.spring.boot.test.ch6.weather.preferences.internal;

import wf.garnier.spring.boot.test.ch6.weather.preferences.Preferences;
import wf.garnier.spring.boot.test.ch6.weather.preferences.PreferencesService;
import wf.garnier.spring.boot.test.ch6.weather.preferences.SortOrder;
import wf.garnier.spring.boot.test.ch6.weather.preferences.UnitSystem;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/preferences")
class PreferencesController {

	private final PreferencesService preferencesService;

	private final PreferencesProperties preferencesProperties;

	public PreferencesController(PreferencesService preferencesService, PreferencesProperties preferencesProperties) {
		this.preferencesService = preferencesService;
		this.preferencesProperties = preferencesProperties;
	}

	@GetMapping
	public PresentationPreferences getPreferences() {
		return new PresentationPreferences(preferencesService.getPreferences(), preferencesProperties.getThreshold());
	}

	@PutMapping
	public PresentationPreferences updatePreferences(@RequestBody PreferencesUpdateRequest request) {
		return new PresentationPreferences(
				preferencesService.updatePreferences(request.darkMode(), request.units(), request.sortBy()),
				preferencesProperties.getThreshold());
	}

	public record PreferencesUpdateRequest(Boolean darkMode, UnitSystem units, SortOrder sortBy) {
	}

	public static record PresentationPreferences(boolean darkMode, UnitSystem units, SortOrder sortBy,
			double coldThreshold, double hotThreshold) {
		public PresentationPreferences(Preferences preferences, PreferencesProperties.Threshold threshold) {
			this(preferences.isDarkMode(), preferences.getUnits(), preferences.getSortBy(), threshold.cold(),
					threshold.hot());
		}
	}

}
