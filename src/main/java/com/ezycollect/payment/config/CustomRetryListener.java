package com.ezycollect.payment.config;

import com.ezycollect.payment.exception.RetriesExhaustedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;

@Slf4j
@Configuration
public class CustomRetryListener extends RetryListenerSupport {

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        log.error("Error invoking URL {}", context.getLastThrowable().getMessage());

        log.warn("Retrying operation for the {} time", context.getRetryCount());
        super.onError(context, callback, throwable);
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if (throwable != null) {
            //TODO: Push to DLQ for retry later
            log.info("Retry operation completed after {} attempts - Payload {}", context.getRetryCount(), context.getAttribute("payload"));
            Object payload = context.getAttribute("payload");
            throw new RetriesExhaustedException("Retries exhausted for operation. Payload: " + (payload != null ? payload.toString() : "N/A"), throwable);
        }
        super.close(context, callback, throwable);
    }
}
