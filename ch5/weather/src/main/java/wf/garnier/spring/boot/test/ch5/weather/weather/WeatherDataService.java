package wf.garnier.spring.boot.test.ch5.weather.weather;

public interface WeatherDataService {

	WeatherData getCurrentWeather(double latitude, double longitude);

}
