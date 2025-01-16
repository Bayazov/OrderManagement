package com.example.ordermanagement.infrastructure.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.example.ordermanagement.application.OrderService.*(..))")
    public void logBeforeAllMethods(JoinPoint joinPoint) {
        logger.info("Executing {} with arguments {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.example.ordermanagement.application.OrderService.*(..))", returning = "result")
    public void logAfterAllMethods(JoinPoint joinPoint, Object result) {
        logger.info("{} returned {}", joinPoint.getSignature().getName(), result);
    }
}

