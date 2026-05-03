package wf.garnier.spring.boot.test.ch5.weather.weather;

import wf.garnier.spring.boot.test.ch5.weather.city.City;

record CityWeather(String cityName, String country, Integer cityId, String weather, Double temperature,
		Double windSpeed) {

	public CityWeather(City city, WeatherData weatherData) {
		this(city.getName(), city.getCountry(), city.getId(), weatherData.weather(), weatherData.temperature(),
				weatherData.windspeed());
	}

}
