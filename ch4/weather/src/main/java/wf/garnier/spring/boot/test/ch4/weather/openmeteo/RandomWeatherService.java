package wf.garnier.spring.boot.test.ch4.weather.openmeteo;

import java.util.Random;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
class RandomWeatherService implements WeatherService {

	private final Random random = new Random();

	@Override
	public WeatherData getCurrentWeather(double latitude, double longitude) {
		return new WeatherData(randomDecimal(), randomDecimal(), randomWeather());
	}

	private double randomDecimal() {
		return random.nextInt(-150, 360) / 10.0;
	}

	private String randomWeather() {
		var possibleWeathers = new String[] { "Clear sky", "Partly cloudy", "Rain" };
		return possibleWeathers[random.nextInt(possibleWeathers.length)];
	}

}
