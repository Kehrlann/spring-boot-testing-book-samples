package wf.garnier.spring.boot.test.ch6.weather.weather.internal;

import org.junit.jupiter.api.Test;
import wf.garnier.spring.boot.test.ch6.weather.weather.WeatherData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(components = { OpenMeteoWeatherDataService.class })
class OpenMeteoWeatherDataServiceTests {

	@Autowired
	private OpenMeteoWeatherDataService service;

	@Autowired
	private MockRestServiceServer mockServer;

	@MockitoBean
	private WeatherServiceProperties props;

	@Test
	void getWeatherData() {
		mockServer.expect(queryParam("latitude", "32.99"))
			.andExpect(queryParam("longitude", "-97.68"))
			.andRespond(withSuccess("""
					{
						"current": {
							"temperature_2m": 3.1,
							"windspeed_10m": 52.0,
							"weathercode": 95
						}
					}
					""", MediaType.APPLICATION_JSON));

		var response = service.getCurrentWeather(32.99, -97.68);

		assertThat(response.temperature()).isEqualTo(3.1);
		assertThat(response.windspeed()).isEqualTo(52.0);
		assertThat(response.weather()).isEqualTo("Thunderstorm");
	}

	@Test
	void http500Recover() {
		mockServer.expect(anything()).andRespond(withServerError());

		var response = service.getCurrentWeather(32.99, -97.68);

		assertThat(response).isEqualTo(new WeatherData());
	}

	@Test
	void http400Throw() {
		//@formatter:off
		mockServer.expect(anything())
				.andRespond(withBadRequest());

		assertThatThrownBy(() -> service.getCurrentWeather(32.99, -97.68))
				.isInstanceOf(IllegalArgumentException.class);
		//@formatter:on
	}

	@Test
	void customResponse() {
		mockServer.expect(anything()).andRespond(withStatus(HttpStatus.I_AM_A_TEAPOT));

		var response = service.getCurrentWeather(32.99, -97.68);

		assertThat(response).isEqualTo(new WeatherData());
	}

}
