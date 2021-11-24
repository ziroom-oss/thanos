package com.ziroom.qa.quality.defende.provider.config.interceptor;

import com.ziroom.qa.quality.defende.provider.entity.User;
import com.ziroom.qa.quality.defende.provider.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public synchronized boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();
        log.info(url);
        log.info("拦截器开始 ------------------------------------------------------ ");
        //1. 获取请求参数
        String userName = request.getHeader("userName");
//        String userToken = request.getHeader("userToken");
//        if (StringUtils.isBlank(userName) || StringUtils.isBlank(userToken)) {
//            return false;
//        }
//        UserVo userVo = userService.getByToken(userToken);
//        if (Objects.isNull(userVo)) {
//            log.info("用户token失效！");
//            return false;
//        }
        User user = userService.getUserInfoByUserName(userName);
        if (Objects.isNull(user)) {
            log.info("{}，该用户不存在！！！", userName);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


}
