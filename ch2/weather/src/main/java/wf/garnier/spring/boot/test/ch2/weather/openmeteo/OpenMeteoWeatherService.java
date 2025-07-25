package wf.garnier.spring.boot.test.ch2.weather.openmeteo;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Profile("!local")
class OpenMeteoWeatherService implements WeatherService {

	private final RestClient restClient;

	private static final String API_URL = "https://api.open-meteo.com/v1/forecast";

	OpenMeteoWeatherService(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.baseUrl(API_URL).build();
	}

	@Override
	public WeatherData getCurrentWeather(double latitude, double longitude) {
		ApiResponse response = restClient.get()
			.uri(uriBuilder -> uriBuilder.queryParam("latitude", latitude)
				.queryParam("longitude", longitude)
				.queryParam("current", "temperature_2m,windspeed_10m,weathercode")
				.build())
			.retrieve()
			.body(ApiResponse.class);
		// TODO: null?
		return new WeatherData(response.current.temperature, response.current.windspeed, response.current.weathercode);
	}

	private record ApiResponse(Current current) {
	}

	private record Current(@JsonProperty("temperature_2m") double temperature,
			@JsonProperty("windspeed_10m") double windspeed, int weathercode) {
	}

}
