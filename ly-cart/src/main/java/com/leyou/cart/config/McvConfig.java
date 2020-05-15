package com.leyou.cart.config;

import com.leyou.cart.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class McvConfig implements WebMvcConfigurer {

    @Autowired
    private JwtProperties prop;

    @Bean
    public LoginInterceptor createLoginInterceptor() {
        return new LoginInterceptor(prop);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(createLoginInterceptor()).addPathPatterns("/**");
    }
}
