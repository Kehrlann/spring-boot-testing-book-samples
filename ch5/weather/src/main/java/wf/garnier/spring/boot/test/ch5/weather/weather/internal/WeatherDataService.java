package wf.garnier.spring.boot.test.ch5.weather.weather.internal;

import wf.garnier.spring.boot.test.ch5.weather.weather.WeatherData;

public interface WeatherDataService {

	WeatherData getCurrentWeather(double latitude, double longitude);

}
