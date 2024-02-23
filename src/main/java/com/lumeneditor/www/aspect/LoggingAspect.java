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

    /**
     * 모든 컨트롤러 메서드 실행 전에 로그를 기록하는 어드바이스입니다.
     * 이 메서드는 실행되는 컨트롤러 메서드의 이름과 전달되는 인자를 로그로 기록합니다.
     * AOP의 포인트컷 표현식을 사용하여 특정 패턴의 메서드에만 이 로깅을 적용합니다.
     *
     * @param joinPoint 실행되는 대상 메서드에 대한 메타데이터를 포함하는 조인 포인트 객체입니다.
     */

    @Before("execution(* com.lumeneditor.www.domain..*Controller.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        log.info("Start: {} with arguments {}",
                joinPoint.getSignature().toShortString(),
                joinPoint.getArgs());
    }


    /**
     * 컨트롤러 메서드가 성공적으로 반환된 후에 로그를 기록하는 어드바이스입니다.
     * 이 메서드는 메서드의 반환 값까지 포함하여 로그를 기록합니다.
     * 반환 값이 있는 메서드의 경우, 이 반환 값을 로그에 포함시켜 어떤 값이 반환되었는지 확인할 수 있습니다.
     *
     * @param joinPoint   실행된 메서드에 대한 정보를 포함하는 조인 포인트 객체입니다.
     * @param returnValue 메서드가 반환한 객체입니다. void 메서드의 경우 이 값은 null입니다.
     */

    @AfterReturning(pointcut = "execution(* com.lumeneditor.www.domain..*Controller.*(..))", returning = "returnValue")
    public void logAfterReturningMethod(JoinPoint joinPoint, Object returnValue) {
        log.info("End: {} with return value {}",
                joinPoint.getSignature().toShortString(),
                returnValue);
    }

    /**
     * 컨트롤러 메서드의 실행 시간을 측정하고 로그를 기록하는 어드바이스입니다.
     * 이 메서드는 메서드 실행 전후의 시간을 측정하여, 메서드 실행에 걸린 총 시간을 로그로 기록합니다.
     * 성능 분석 및 최적화에 유용한 데이터를 제공할 수 있습니다.
     *
     * @param joinPoint 실행되는 대상 메서드에 대한 정보를 포함하는 조인 포인트 객체입니다.
     * @return 대상 메서드의 실행 결과를 반환합니다.
     * @throws Throwable 대상 메서드 실행 중 발생한 예외를 그대로 던집니다.
     */

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