//package com.ziroom.qa.quality.defende.provider.config.cross;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// *
// * 通过Filter实现全局跨域支持
// */
//@Slf4j
//@Component
//public class CorsConfig implements Filter {
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        log.info("CorsConfig init");
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        log.info("CorsConfig doFilter");
//        HttpServletRequest request1 = (HttpServletRequest) request;
//        HttpServletResponse response1 = (HttpServletResponse) response;
//
//        if (request1.getHeader("Origin")!=null){
//            response1.setHeader("Access-Controller-Allow-Origin"
//                    ,request1.getHeader("Origin"));
//        }
//
//        response1.setHeader("Access-Control-Allow-Credentials","true");
//        response1.setHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS,HEAD");
//        response1.setHeader("Access-Control-Max-Age","3600");
//        response1.setHeader("Access-Control-Allow-Headers","Content-Type, Accept, X-Requested-With, remember-me");
//
//        chain.doFilter(request,response);
//    }
//
//    @Override
//    public void destroy() {
//        log.info("CorsConfig destroy");
//    }
//}
