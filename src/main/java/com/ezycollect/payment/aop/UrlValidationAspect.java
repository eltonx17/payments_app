package com.ezycollect.payment.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.regex.Pattern;

@Aspect
@Component
@Slf4j
public class UrlValidationAspect {


    @Pointcut("execution(* com.ezycollect.payment.service.WebhookService.registerWebhook(String)) && args(url)")
    public void registerWebhookMethod(String url) {}

    /**
     * Validate URL format before registering a webhook
     * @param url
     */
    @Before("registerWebhookMethod(url)")
    public void validateUrl(String url) {
        log.info("Validating URL: {}", url);
        UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https", "ftp"});
        if (!urlValidator.isValid(url)) {
            log.error("Invalid URL received: {}", url);
            throw new IllegalArgumentException("Invalid URL format: " + url);
        }
        log.info("URL is valid: {}", url);
    }
}
