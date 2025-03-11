package wf.garnier.spring.boot.test.ch2.weather.openmeteo;

public interface WeatherService {

	WeatherResponse getWeather(double latitude, double longitude);

}
