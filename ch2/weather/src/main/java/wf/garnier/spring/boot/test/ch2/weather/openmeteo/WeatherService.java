package wf.garnier.spring.boot.test.ch2.weather.openmeteo;

public interface WeatherService {

	WeatherData getCurrentWeather(double latitude, double longitude);

}
