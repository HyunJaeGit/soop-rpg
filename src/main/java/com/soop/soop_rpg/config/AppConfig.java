package com.soop.soop_rpg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * [클래스 역할]: 애플리케이션의 전반적인 설정을 담당하는 클래스입니다.
 */
@Configuration // 스프링이 "이 클래스는 설정 파일이야!"라고 인식하게 해주는 어노테이션입니다.
public class AppConfig {

    /**
     * [메서드 역할]: 외부 API와 통신할 때 사용하는 'RestTemplate'이라는 도구를 생성합니다.
     * @Bean: 스프링이 이 메서드를 실행해서 만든 객체를 직접 관리하도록 등록하는 것입니다.
     * 이렇게 해두면 다른 클래스에서 이 '전화기(RestTemplate)'를 빌려 쓸 수 있습니다.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}