package wf.garnier.spring.boot.test.ch6.weather.weather.internal;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(WeatherServiceProperties.class)
class WeatherConfiguration {

}
