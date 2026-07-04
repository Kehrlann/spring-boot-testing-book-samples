package wf.garnier.spring.boot.test.ch6.weather.weather.internal;

import wf.garnier.spring.boot.test.ch6.weather.city.City;
import wf.garnier.spring.boot.test.ch6.weather.weather.WeatherData;

public record CityWeather(String cityName, String country, Integer cityId, String weather, Double temperature,
		Double windSpeed) {

	public CityWeather(City city, WeatherData weatherData) {
		this(city.getName(), city.getCountry(), city.getId(), weatherData.weather(), weatherData.temperature(),
				weatherData.windspeed());
	}

}
