package wf.garnier.spring.boot.test.ch3.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WidgetConfiguration {

	@Bean
	Widget blueWidget() {
		return new Widget("blue");
	}

	@Bean
	Widget blackWidget() {
		return new Widget("black");
	}

}
