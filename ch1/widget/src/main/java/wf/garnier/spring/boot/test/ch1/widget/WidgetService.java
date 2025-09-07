package wf.garnier.spring.boot.test.ch1.widget;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
class WidgetService {

	private final WidgetRepository widgetRepository;

	private final WidgetValidator widgetValidator;

	public WidgetService(WidgetRepository widgetRepository, WidgetValidator widgetValidator) {
		this.widgetRepository = widgetRepository;
		this.widgetValidator = widgetValidator;
	}

	public List<Widget> listWidgets() {
		return widgetRepository.findAll();
	}

	public Optional<Widget> findWidget(int id) {
		return widgetRepository.findById(id);
	}

	public Widget createWidget(String name) throws InvalidWidgetException {
		widgetValidator.validateWidget(name);
		return widgetRepository.createWidget(name);
	}

}
