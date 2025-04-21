package com.meetime.hubspot.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;

@Configuration
public class ResilienceConfig {

    @Bean
    RateLimiter rateLimiter() {
		
		RateLimiterConfig config = RateLimiterConfig.custom()
				.limitForPeriod(110) //Limit of 110 requests per period
				.limitRefreshPeriod(Duration.ofSeconds(10)) //Resets the limit every 1 second
				.timeoutDuration(Duration.ofMillis(500)) //Timeout to wait for a new request
				.build();
		
		return RateLimiter.of("hubspotRateLimiter", config);
	}

    @Bean
    Retry retry() {
		
		RetryConfig config = RetryConfig.custom()
				.maxAttempts(3) //Maximum of 3 attempts
				.waitDuration(Duration.ofSeconds(2)) //Wait 2 seconds for each attempt
				.build();
		
		return Retry.of("hubspotRetry", config);
	}

}
