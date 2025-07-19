package wf.garnier.spring.boot.test.ch3.inventory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ThingConfiguration {
    @Bean
    Thing one() {
        return new Thing("one");
    }

    @Bean
    Thing two() {
        return new Thing("two");
    }
}
