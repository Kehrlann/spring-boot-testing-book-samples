package wf.garnier.spring.boot.test.ch3.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "demo")
record DemoProperties(String message, Integer value, Boolean active) {

}
