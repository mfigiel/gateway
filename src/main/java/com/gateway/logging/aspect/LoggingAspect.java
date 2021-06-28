package com.gateway.logging.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Aspect
public class LoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

   // @Around("(execution(* com.gateway.api.controller..*(..)))")
    public Object log(ProceedingJoinPoint jp) throws Throwable {
        Object result;
        final long start = System.currentTimeMillis();
        try {
        result = jp.proceed();
        long executionTime = System.currentTimeMillis() - start;
        LOGGER.info("{} {} took {} milliseconds",
                logString(result), jp.getSignature().toShortString(), executionTime);

        } catch (final Throwable ex) {
             LOGGER.warn("{} threw {} took {} milliseconds",
                     jp.getSignature().toShortString(), ex.getClass().getSimpleName(),
                     (System.currentTimeMillis() - start));
              throw ex;
          }
        return result;
    }

    private String logString(Object result) {
        return String.format("The result was: %s", result != null ? result.toString() : "NULL");
    }
}
