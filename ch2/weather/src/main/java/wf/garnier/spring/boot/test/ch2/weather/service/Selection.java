package wf.garnier.spring.boot.test.ch2.weather.service;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import wf.garnier.spring.boot.test.ch2.weather.city.City;

@Entity
@Table(name = "preferred_city")
public class Selection {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "city_id", nullable = false)
	private City city;

	// JPA requires default constructor
	protected Selection() {
	}

	public Selection(City city) {
		this.city = city;
	}

	public Long getId() {
		return id;
	}

	public City getCity() {
		return city;
	}

	@Override
	public String toString() {
		return "Selection{" +
			   "city=" + city.getName() +
			   '}';
	}
}