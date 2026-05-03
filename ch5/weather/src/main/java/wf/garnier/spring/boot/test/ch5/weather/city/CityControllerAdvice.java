package wf.garnier.spring.boot.test.ch5.weather.city;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class CityControllerAdvice {

	@ExceptionHandler(CityNotFoundException.class)
	public ResponseEntity<String> handleCityNotFound(CityNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(CityAlreadySelectedException.class)
	public ResponseEntity<String> handleCityAlreadySelected(CityAlreadySelectedException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body("City already selected");
	}

}
