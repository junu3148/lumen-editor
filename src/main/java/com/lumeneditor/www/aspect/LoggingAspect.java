package com.lumeneditor.www.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // 모든 컨트롤러의 메서드 실행 전에 로그 기록
    @Before("execution(* com.lumeneditor.www.domain..*Controller.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        log.info("Start: {} with arguments {}",
                joinPoint.getSignature().toShortString(),
                joinPoint.getArgs());
    }

    // 메서드가 성공적으로 반환된 후 로그 기록 (리턴 값을 포함)
    @AfterReturning(pointcut = "execution(* com.lumeneditor.www.domain..*Controller.*(..))", returning = "returnValue")
    public void logAfterReturningMethod(JoinPoint joinPoint, Object returnValue) {
        log.info("End: {} with return value {}",
                joinPoint.getSignature().toShortString(),
                returnValue);
    }

    // 모든 컨트롤러의 메서드 실행 전후에 시간 계산
    @Around("execution(* com.lumeneditor.www.domain..*Controller.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis(); // 메서드 실행 전 시간 측정
        try {
            return joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - start; // 메서드 실행 후 시간 측정 및 소요 시간 계산
            log.info("in {} ms",
                    executionTime); // 로그 기록
        }
    }
}