package wf.garnier.spring.boot.test.ch5.weather.preferences.internal;

import wf.garnier.spring.boot.test.ch5.weather.preferences.Preferences;
import wf.garnier.spring.boot.test.ch5.weather.preferences.PreferencesService;
import wf.garnier.spring.boot.test.ch5.weather.preferences.SortOrder;
import wf.garnier.spring.boot.test.ch5.weather.preferences.UnitSystem;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/preferences")
class PreferencesController {

	private final PreferencesService preferencesService;

	public PreferencesController(PreferencesService preferencesService) {
		this.preferencesService = preferencesService;
	}

	@GetMapping
	public Preferences getPreferences() {
		return preferencesService.getPreferences();
	}

	@PutMapping
	public Preferences updatePreferences(@RequestBody PreferencesUpdateRequest request) {
		return preferencesService.updatePreferences(request.darkMode(), request.units(), request.sortBy());
	}

	public record PreferencesUpdateRequest(Boolean darkMode, UnitSystem units, SortOrder sortBy) {
	}

}
