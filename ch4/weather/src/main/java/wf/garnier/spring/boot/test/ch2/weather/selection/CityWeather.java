package wf.garnier.spring.boot.test.ch2.weather.selection;

import wf.garnier.spring.boot.test.ch2.weather.city.City;
import wf.garnier.spring.boot.test.ch2.weather.openmeteo.WeatherData;

public record CityWeather(String cityName, String country, Long cityId, String weather, Double temperature,
		Double windSpeed) {

	public CityWeather(City city, WeatherData weatherData) {
		this(city.getName(), city.getCountry(), city.getId(), weatherData.weather(), weatherData.temperature(),
				weatherData.windspeed());
	}

}
