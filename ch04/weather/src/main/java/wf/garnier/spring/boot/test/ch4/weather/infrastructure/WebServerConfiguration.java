package wf.garnier.spring.boot.test.ch4.weather.infrastructure;

import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class WebServerConfiguration {

	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> connectorCustomizer(
			TomcatAccessRepository tomcatAccessRepository) {
		return factory -> factory.addContextValves(tomcatAccessRepository);
	}

}
