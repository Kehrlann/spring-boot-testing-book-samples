package wf.garnier.spring.boot.test.ch7.weather.city.internal;

import java.time.Instant;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import wf.garnier.spring.boot.test.ch7.weather.security.UserId;

@Entity
@Table(name = "preferred_city")
public class SelectedCity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * The user this selection belongs to.
	 */
	@Embedded
	@AttributeOverride(name = "value", column = @Column(name = "user_id", nullable = false))
	private UserId user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "city_id", nullable = false)
	private CityEntity city;

	private Instant dateAdded;

	// JPA requires default constructor
	protected SelectedCity() {
	}

	public SelectedCity(UserId user, CityEntity city) {
		this.user = user;
		this.city = city;
		this.dateAdded = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public UserId getUser() {
		return user;
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
		return "Selection{" + "owner=" + user + ", city=" + city.getName() + '}';
	}

}
