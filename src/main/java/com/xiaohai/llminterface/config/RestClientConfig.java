package com.xiaohai.llminterface.config;

/**
 * @Description:
 * @Author: XiaoYunTao
 * @Date: 2024/10/12
 */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
