package com.sysdesign.banking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RequestLoggingInterceptor interceptor;
    private final ChaosInterceptor chaosInterceptor;

    public WebConfig(RequestLoggingInterceptor interceptor, ChaosInterceptor chaosInterceptor) {
        this.interceptor = interceptor;
        this.chaosInterceptor = chaosInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(chaosInterceptor);
        registry.addInterceptor(interceptor);

    }
}
