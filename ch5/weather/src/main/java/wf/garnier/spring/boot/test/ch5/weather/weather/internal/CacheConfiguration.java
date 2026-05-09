package wf.garnier.spring.boot.test.ch5.weather.weather.internal;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.cache.autoconfigure.CacheManagerCustomizer;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableCaching
class CacheConfiguration {

	static final String WEATHER_CACHE_NAME = "weather";

	private static final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

	@Bean
	public CacheManagerCustomizer<ConcurrentMapCacheManager> cacheManagerCustomizer() {
		return (cacheManager) -> cacheManager.setCacheNames(List.of(WEATHER_CACHE_NAME));
	}

	@Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
	@CacheEvict(value = WEATHER_CACHE_NAME, allEntries = true)
	public void evictCache() {
		log.debug("Evicting cache");
	}

}
