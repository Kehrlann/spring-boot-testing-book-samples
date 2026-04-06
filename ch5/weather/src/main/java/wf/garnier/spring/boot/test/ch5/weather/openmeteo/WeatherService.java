package wf.garnier.spring.boot.test.ch5.weather.openmeteo;

public interface WeatherService {

	WeatherData getCurrentWeather(double latitude, double longitude);

}
