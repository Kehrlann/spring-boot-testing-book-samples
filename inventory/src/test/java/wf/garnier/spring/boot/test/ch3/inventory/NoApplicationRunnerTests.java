package wf.garnier.spring.boot.test.ch3.inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class NoApplicationRunnerTests {

    @Test
    void contextLoads(CapturedOutput output) {
        assertThat(output).doesNotContain("Thing[name=");
    }

    @Configuration
    static class TestConfig {

        @Bean
        public Thing three() {
            return new Thing("test");
        }

    }
}
