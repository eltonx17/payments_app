package com.ezycollect.payment.util;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UrlValidationUtils {

    /**
     * Validate URL format before registering a webhook
     * URL must start with http, https or ftp, or be a simple www.url.com style URL.
     * Uses Apache Commons UrlValidator for robust validation.
     * @param url The URL to validate.
     */
    public static boolean validateUrl(String url) {
        log.info("Validating URL: {}", url);
        if (StringUtils.isEmpty(url)) {
            log.error("URL is empty or null");
            return false;
        }

        UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https", "ftp"});
        if (!urlValidator.isValid(url)) {
            log.error("Invalid URL received: {}", url);
            return false;
        }
        log.info("URL is valid: {}", url);
        return true;
    }
}
