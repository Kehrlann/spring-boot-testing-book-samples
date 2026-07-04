package wf.garnier.spring.boot.test.ch1.widget;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class WidgetValidator {

	private final Random random = new Random();

	public void validateWidget(String name) throws InvalidWidgetException {
		if (random.nextInt(5) == 0) {
			throw new InvalidWidgetException("Invalid widget, for some random reason");
		}
	}

}
