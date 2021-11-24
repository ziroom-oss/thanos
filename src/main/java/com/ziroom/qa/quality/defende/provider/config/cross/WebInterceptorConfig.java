package com.ziroom.qa.quality.defende.provider.config.cross;

import com.ziroom.qa.quality.defende.provider.config.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {

            /**
             * 添加拦截器
             * @param registry
             */
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(loginInterceptor)
                        .addPathPatterns("/**")
                        .excludePathPatterns("/testStatistic/*", "/outTestTask/*", "/swagger-ui.html", "/webjars/**", "/swagger-resources/**", "/error/**", "/api-docs/**", "/v2/**", "/csrf");
            }

//            /**
//             * 拦截器实现 跨域支持
//             * @param registry
//             */
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("*")
//                        .allowCredentials(true)
//                        .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS", "HEAD")
//                        .allowedHeaders("*")
//                        .maxAge(3600);
//            }
        };
    }
}
