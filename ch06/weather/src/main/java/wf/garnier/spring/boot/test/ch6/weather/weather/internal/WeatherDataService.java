package wf.garnier.spring.boot.test.ch6.weather.weather.internal;

import wf.garnier.spring.boot.test.ch6.weather.weather.WeatherData;

public interface WeatherDataService {

	WeatherData getCurrentWeather(double latitude, double longitude);

}
