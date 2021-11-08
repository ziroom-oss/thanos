package com.ziroom.qa.quality.defende.provider.config.cross;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局跨域
 * CorsFilter 实现了Filter
 * <p>
 * ：一个http请求，先走filter，到达servlet后才进行拦截器的处理，
 * 所以我们可以把cors放在filter里，就可以优先于权限拦截器执行。
 */
@Slf4j
@Configuration
public class GlobalCorsConfig {
    //允许跨域调用的过滤器
    @Bean
    public CorsFilter corsFilter() {
        log.info("TokenConfig corsFilter");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", addcorsConfig());
        return new CorsFilter(source);
    }

    private CorsConfiguration addcorsConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        List<String> list = new ArrayList<>();
        list.add("*");
        corsConfiguration.setAllowedOrigins(list);
    /*
    // 请求常用的三种配置，*代表允许所有，
    //当时你也可以自定义属性（比如header只能带什么，只能是post方式等等）
    */
        //允许跨越发送cookie
        corsConfiguration.setAllowCredentials(true);
        //允许所有域名进行跨域调用
        corsConfiguration.addAllowedOrigin("*");
        //放行全部原始头信息
        corsConfiguration.addAllowedHeader("*");
        //允许所有请求方法跨域调用
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }
}
