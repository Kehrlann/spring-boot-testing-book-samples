package wf.garnier.spring.boot.test.ch2.weather.openmeteo;

public record WeatherResponse(double temperature, double windspeed, int weathercode) {
	// Weather codes as per https://open-meteo.com/en/docs
	public String getWeatherDescription() {
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