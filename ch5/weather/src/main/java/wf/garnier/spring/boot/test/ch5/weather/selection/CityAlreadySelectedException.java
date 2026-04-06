package wf.garnier.spring.boot.test.ch5.weather.selection;

public class CityAlreadySelectedException extends RuntimeException {
	public CityAlreadySelectedException(long id) {
		super("City with id " + id + " is already selected");
	}
}
