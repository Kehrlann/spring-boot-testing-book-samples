package wf.garnier.spring.boot.test.ch2.weather.weather.service;

import wf.garnier.spring.boot.test.ch2.weather.weather.model.WeatherResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WeatherService {

	private final RestClient restClient;

	private static final String API_URL = "https://api.open-meteo.com/v1/forecast";

	public WeatherService(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.baseUrl(API_URL).build();
	}

	public WeatherResponse getWeather(double latitude, double longitude) {
		ApiResponse response = restClient.get()
			.uri(uriBuilder -> uriBuilder.queryParam("latitude", latitude)
				.queryParam("longitude", longitude)
				.queryParam("current", "temperature_2m,windspeed_10m,weathercode")
				.build())
			.retrieve()
			.body(ApiResponse.class);

		return new WeatherResponse(response.current.temperature_2m, response.current.windspeed_10m,
				response.current.weathercode);
	}

	private record ApiResponse(Current current) {
	}

	private record Current(double temperature_2m, double windspeed_10m, int weathercode) {
		double getTemperature() {
			return temperature_2m;
		}

		double getWindspeed() {
			return windspeed_10m;
		}
	}

}
