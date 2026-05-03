package wf.garnier.spring.boot.test.ch5.weather.weather;

import java.util.List;

import wf.garnier.spring.boot.test.ch5.weather.selection.CityService;

import org.springframework.stereotype.Service;

@Service
public class WeatherService {

	private final WeatherDataService weatherDataService;

	private final CityService cityService;

	public WeatherService(WeatherDataService weatherDataService, CityService cityService) {
		this.weatherDataService = weatherDataService;
		this.cityService = cityService;
	}

	public List<CityWeather> getWeatherInSelectedCities() {
		//@formatter:off
        return cityService.getSelectedCities()
                .stream()
                .map(city -> {
                    var weatherData = weatherDataService.getCurrentWeather(city.getLatitude(), city.getLongitude());
                    return new CityWeather(city, weatherData);
                }).toList();
        //@formatter:on
	}

}
