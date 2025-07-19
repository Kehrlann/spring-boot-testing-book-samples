package wf.garnier.spring.boot.test.ch3.inventory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TestSpecificConfiguration {

    @Bean
    Thing configurationThing() {
        return new Thing("configuration-test-package");
    }
}
