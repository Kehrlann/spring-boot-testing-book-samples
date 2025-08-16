package wf.garnier.spring.boot.test.ch4.weather.openmeteo;

public interface WeatherService {

	WeatherData getCurrentWeather(double latitude, double longitude);

}
