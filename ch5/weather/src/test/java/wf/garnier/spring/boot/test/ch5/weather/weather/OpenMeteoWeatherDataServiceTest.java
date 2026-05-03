package wf.garnier.spring.boot.test.ch5.weather.weather;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(components = { OpenMeteoWeatherDataService.class })
class OpenMeteoWeatherDataServiceTest {

	@Autowired
	private OpenMeteoWeatherDataService service;

	@Autowired
	private MockRestServiceServer mockServer;

	@Test
	void getWeatherData() {
		mockServer.expect(queryParam("latitude", "32.99"))
			.andExpect(queryParam("longitude", "-97.68"))
			.andRespond(withSuccess("""
					{
						"current": {
							"temperature_2m": 22.2,
							"windspeed_10m": 1.1,
							"weathercode": 99
						}
					}
					""", MediaType.APPLICATION_JSON));

		var response = service.getCurrentWeather(32.99, -97.68);

		assertThat(response).isEqualTo(new WeatherData(22.2, 1.1, 99));
	}

	@Test
	void http500Recover() {
		mockServer.expect(anything()).andRespond(withServerError());

		var response = service.getCurrentWeather(32.99, -97.68);

		assertThat(response).isEqualTo(new WeatherData());
	}

	@Test
	void http400Throw() {
		mockServer.expect(anything()).andRespond(withBadRequest());

		assertThatThrownBy(() -> service.getCurrentWeather(32.99, -97.68)).isInstanceOf(IllegalStateException.class);
	}

}
