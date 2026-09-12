package wf.garnier.spring.boot.test.ch7.weather.preferences.internal;

import wf.garnier.spring.boot.test.ch7.weather.preferences.Preferences;
import wf.garnier.spring.boot.test.ch7.weather.preferences.PreferencesService;
import wf.garnier.spring.boot.test.ch7.weather.preferences.SortOrder;
import wf.garnier.spring.boot.test.ch7.weather.preferences.UnitSystem;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;

import org.springframework.security.core.Authentication;
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
	public PresentationPreferences getPreferences(Authentication authentication) {
		return new PresentationPreferences(preferencesService.getPreferences(UserId.of(authentication)),
				preferencesProperties.getTemperatureThreshold());
	}

	@PutMapping
	public PresentationPreferences updatePreferences(Authentication authentication,
			@RequestBody PreferencesUpdateRequest request) {
		return new PresentationPreferences(preferencesService.updatePreferences(UserId.of(authentication),
				request.darkMode(), request.units(), request.sortBy()),
				preferencesProperties.getTemperatureThreshold());
	}

	public record PreferencesUpdateRequest(Boolean darkMode, UnitSystem units, SortOrder sortBy) {
	}

	public static record PresentationPreferences(boolean darkMode, UnitSystem units, SortOrder sortBy,
			double coldThreshold, double hotThreshold) {
		public PresentationPreferences(Preferences preferences,
				PreferencesProperties.TemperatureThreshold temperatureThreshold) {
			this(preferences.isDarkMode(), preferences.getUnits(), preferences.getSortBy(), temperatureThreshold.cold(),
					temperatureThreshold.hot());
		}
	}

}
