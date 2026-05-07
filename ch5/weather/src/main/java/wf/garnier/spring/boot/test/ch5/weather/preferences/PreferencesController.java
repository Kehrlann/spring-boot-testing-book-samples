package wf.garnier.spring.boot.test.ch5.weather.preferences;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/preferences")
public class PreferencesController {

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
