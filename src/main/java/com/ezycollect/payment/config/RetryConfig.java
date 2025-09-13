package com.ezycollect.payment.config;

import org.springframework.beans.factory.config.BeanDefinition;
import com.ezycollect.payment.config.CustomRetryListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
public class RetryConfig {

    @Value("${webhook.max-retries:3}")
    private int maxRetries;

    @Value("${webhook.initial-backoff-ms:500}")
    private long initialBackoffMs;

    @Bean
        public RetryTemplate retryTemplate(CustomRetryListener customRetryListener) {
            RetryTemplate retryTemplate = new RetryTemplate();

            ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
            backOffPolicy.setInitialInterval(initialBackoffMs);
            backOffPolicy.setMultiplier(2);
            retryTemplate.setBackOffPolicy(backOffPolicy);

            SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
            retryPolicy.setMaxAttempts(maxRetries);
            retryTemplate.setRetryPolicy(retryPolicy);

            retryTemplate.setListeners(new CustomRetryListener[]{customRetryListener});

            return retryTemplate;
        }
    }