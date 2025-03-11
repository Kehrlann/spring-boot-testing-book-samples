package wf.garnier.spring.boot.test.ch2.weather.openmeteo;

import com.fasterxml.jackson.annotation.JsonCreator;

public record WeatherData(double temperature, double windspeed, String weather) {

	@JsonCreator
	public WeatherData(double temperature, double windspeed, int weathercode) {
		this(temperature, windspeed, weatherFromCode(weathercode));
	}

	// Weather codes as per https://open-meteo.com/en/docs
	public static String weatherFromCode(int weathercode) {
		return switch (weathercode) {
			case 0 -> "Clear sky";
			case 1, 2, 3 -> "Partly cloudy";
			case 45, 48 -> "Foggy";
			case 51, 53, 55 -> "Drizzle";
			case 61, 63, 65 -> "Rain";
			case 71, 73, 75 -> "Snow";
			case 77 -> "Snow grains";
			case 85, 86 -> "Snow showers";
			case 95 -> "Thunderstorm";
			case 96, 99 -> "Thunderstorm with hail";
			default -> "Unknown";
		};
	}
}