package wf.garnier.spring.boot.test.ch6.weather.city.internal;

import wf.garnier.spring.boot.test.ch6.weather.city.CityAlreadySelectedException;
import wf.garnier.spring.boot.test.ch6.weather.city.CityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Controller advice for handling city-related exceptions. In such a small example,
 * exception handlers could live inside the {@link CityController}
 */
//@formatter:off
@ControllerAdvice
class CityControllerAdvice {

	@ExceptionHandler(CityAlreadySelectedException.class)
	ResponseEntity<String> handleCityAlreadySelected(
			CityAlreadySelectedException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body("City already selected");
	}

	@ExceptionHandler(CityNotFoundException.class)
	ResponseEntity<String> handleCityNotFound(CityNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

}
//@formatter:on
