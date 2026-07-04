package wf.garnier.spring.boot.test.ch5.weather.preferences.internal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferencesRepository extends JpaRepository<PreferencesEntity, Long> {

}
