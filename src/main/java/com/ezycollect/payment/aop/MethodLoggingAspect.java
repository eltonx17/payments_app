package com.ezycollect.payment.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class MethodLoggingAspect {

    @Pointcut("execution(public * com.ezycollect.payment.service..*(..))")
    public void serviceMethods() {}


    /**
     * Logs entry and exit of all public methods in the service package
     * along with method arguments and return values.
     * Purely for debugging purposes, only visible when debug logs are enabled.
     */
    @Around("serviceMethods()")
    public Object logEntryExit(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        log.debug("Entering {} with args: {}", methodName, Arrays.toString(args));
        try {
            Object result = joinPoint.proceed();
            log.debug("Exiting {} with result: {}", methodName, result);
            return result;
        } catch (Throwable t) {
            log.debug("Exception in {}: {}", methodName, t.getMessage());
            throw t;
        }
    }
}

