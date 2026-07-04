package wf.garnier.spring.boot.test.ch6.weather.weather.internal;

import java.util.Random;

import wf.garnier.spring.boot.test.ch6.weather.weather.WeatherData;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import static wf.garnier.spring.boot.test.ch6.weather.weather.internal.CacheConfiguration.WEATHER_CACHE_NAME;

@Service
@Profile("local")
public class RandomWeatherDataService implements WeatherDataService {

	private final Random random = new Random();

	@Override
	@Cacheable(WEATHER_CACHE_NAME)
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
