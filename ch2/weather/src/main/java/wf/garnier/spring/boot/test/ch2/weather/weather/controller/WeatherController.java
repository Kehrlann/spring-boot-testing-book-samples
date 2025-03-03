package wf.garnier.spring.boot.test.ch2.weather.weather.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.City;
import wf.garnier.spring.boot.test.ch2.weather.weather.model.WeatherResponse;
import wf.garnier.spring.boot.test.ch2.weather.weather.repository.CityRepository;
import wf.garnier.spring.boot.test.ch2.weather.weather.service.WeatherService;

import java.util.List;

@Controller
@RequestMapping("/")
public class WeatherController {
    private final CityRepository cityRepository;
    private final WeatherService weatherService;

    public WeatherController(CityRepository cityRepository, WeatherService weatherService) {
        this.cityRepository = cityRepository;
        this.weatherService = weatherService;
    }

    @GetMapping
    public String index(Model model) {
        List<City> cities = cityRepository.findAll();
        var citiesWithWeather = cities.stream()
            .map(city -> new CityWeather(
                city,
                weatherService.getWeather(city.getLatitude(), city.getLongitude())
            ))
            .toList();
        
        model.addAttribute("cities", citiesWithWeather);
        return "index";
    }

    @PostMapping("/cities")
    public String addCity(@RequestParam String name,
                         @RequestParam double latitude,
                         @RequestParam double longitude) {
        cityRepository.save(new City(name, latitude, longitude));
        return "redirect:/";
    }

    @PostMapping("/cities/{id}/delete")
    public String deleteCity(@PathVariable Long id) {
        cityRepository.deleteById(id);
        return "redirect:/";
    }

    record CityWeather(City city, WeatherResponse weather) {}
}