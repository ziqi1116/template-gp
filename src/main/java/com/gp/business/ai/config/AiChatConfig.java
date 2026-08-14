package com.gp.business.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * AI 模块专用 RestTemplate
 *
 * 说明：单独定义而不是复用全局 Bean，是因为调用大模型接口需要更长的
 * 读超时（流式回答可能持续几十秒），避免影响系统内其它 HTTP 调用。
 */
@Configuration
public class AiChatConfig {

    @Bean
    public RestTemplate aiRestTemplate(AiChatProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(properties.getTimeoutMillis().intValue());
        return new RestTemplate(factory);
    }

}
