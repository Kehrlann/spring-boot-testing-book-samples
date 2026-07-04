package wf.garnier.spring.boot.test.ch6.weather.city;

public class CityAlreadySelectedException extends RuntimeException {

	public CityAlreadySelectedException(long id) {
		super("City with id " + id + " is already selected");
	}

}
