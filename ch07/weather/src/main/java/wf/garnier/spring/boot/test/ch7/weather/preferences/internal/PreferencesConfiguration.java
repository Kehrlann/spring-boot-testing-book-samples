package wf.garnier.spring.boot.test.ch7.weather.preferences.internal;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PreferencesProperties.class)
class PreferencesConfiguration {

}
