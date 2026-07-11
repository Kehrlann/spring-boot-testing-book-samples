package wf.garnier.spring.boot.test.ch6.weather.weather.internal;

import org.hibernate.validator.constraints.URL;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "weather-service")
class WeatherServiceProperties {

	@URL(regexp = "(?i)^https?:\\/\\/.*")
	private String url = "https://api.open-meteo.com/v1/forecast";

	public WeatherServiceProperties(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

}
