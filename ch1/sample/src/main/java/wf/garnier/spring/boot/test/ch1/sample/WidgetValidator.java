package wf.garnier.spring.boot.test.ch1.sample;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
class WidgetValidator {
    Random random = new Random();

    public void validateWidget(String name) throws InvalidWidgetException {
        if (random.nextInt(5) == 0) {
            throw new InvalidWidgetException("Invalid widget, for some random reason");
        }
    }
}
