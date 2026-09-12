package wf.garnier.spring.boot.test.ch7.weather.preferences.internal;

import java.util.Optional;

import wf.garnier.spring.boot.test.ch7.weather.security.UserId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferencesRepository extends JpaRepository<PreferencesEntity, Long> {

	Optional<PreferencesEntity> findByOwner(UserId owner);

	void deleteByOwner(UserId owner);

}
