package wf.garnier.spring.boot.test.ch5.weather.city.internal;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "preferred_city")
public class SelectedCity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "city_id", nullable = false)
	private CityEntity city;

	private Instant dateAdded;

	// JPA requires default constructor
	protected SelectedCity() {
	}

	public SelectedCity(CityEntity city) {
		this.city = city;
		this.dateAdded = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public CityEntity getCity() {
		return city;
	}

	public Instant getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Instant dateAdded) {
		this.dateAdded = dateAdded;
	}

	@Override
	public String toString() {
		return "Selection{" + "city=" + city.getName() + '}';
	}

}