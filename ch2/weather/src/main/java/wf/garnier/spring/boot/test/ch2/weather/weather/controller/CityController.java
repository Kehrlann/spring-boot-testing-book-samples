package wf.garnier.spring.boot.test.ch2.weather.weather.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.City;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.PreferredCity;
import wf.garnier.spring.boot.test.ch2.weather.weather.repository.CityRepository;
import wf.garnier.spring.boot.test.ch2.weather.weather.repository.PreferredCityRepository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/cities")
public class CityController {
    private final CityRepository cityRepository;
    private final PreferredCityRepository preferredCityRepository;

    public CityController(CityRepository cityRepository, PreferredCityRepository preferredCityRepository) {
        this.cityRepository = cityRepository;
        this.preferredCityRepository = preferredCityRepository;
    }

    @GetMapping
    @ResponseBody
    public List<City> listAllCities() {
        return cityRepository.findAll();
    }

    @PostMapping("/{cityId}/prefer")
    public String addToPreferred(@PathVariable Long cityId) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));
        
        if (preferredCityRepository.findByCityId(cityId).isEmpty()) {
            preferredCityRepository.save(new PreferredCity(city));
        }
        
        return "redirect:/";
    }

    @PostMapping("/{cityId}/unprefer")
    public String removeFromPreferred(@PathVariable Long cityId) {
        preferredCityRepository.findByCityId(cityId)
                .ifPresent(preferredCityRepository::delete);
        return "redirect:/";
    }
}