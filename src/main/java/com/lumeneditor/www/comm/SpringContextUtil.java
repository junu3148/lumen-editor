package com.lumeneditor.www.comm;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public synchronized void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.context = applicationContext;
    }


    /**
     * 스프링 애플리케이션 컨텍스트에서 주어진 클래스 유형의 빈을 가져옵니다.
     * <p>
     * 이 메서드는 스프링 애플리케이션 컨텍스트가 초기화된 후에 호출되어야 합니다.
     * ApplicationContext가 설정되지 않은 경우 IllegalStateException을 발생시킵니다.
     * <p>
     * @param <T> 요청된 빈의 클래스 유형
     * @param beanClass 가져오고자 하는 빈의 클래스 객체
     * @return 요청된 유형의 스프링 관리 빈 인스턴스
     * @throws IllegalStateException ApplicationContext가 설정되지 않았을 경우
     */

    public static <T> T getBean(Class<T> beanClass) {
        if (context == null) {
            throw new IllegalStateException("ApplicationContext has not been set.");
        }
        return context.getBean(beanClass);
    }
}
