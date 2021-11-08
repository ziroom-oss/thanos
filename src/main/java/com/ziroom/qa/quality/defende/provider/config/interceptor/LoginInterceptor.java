package com.ziroom.qa.quality.defende.provider.config.interceptor;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ziroom.qa.quality.defende.provider.constant.enums.UserRoleEnum;
import com.ziroom.qa.quality.defende.provider.entity.User;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.MatrixService;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.service.UserService;
import com.ziroom.qa.quality.defende.provider.vo.MatrixUserDetail;
import com.ziroom.qa.quality.defende.provider.vo.user.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Autowired
    private MatrixService matrixService;

    @Override
    public synchronized boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();
        log.info(url);
        log.info("拦截器开始 ------------------------------------------------------ ");
//        if(url.contains("/swagger-resources")||url.contains("swagger-ui")){
//            return true;
//        }
        //1. 获取请求参数
        String uid = request.getHeader("uid");
        String userName = request.getHeader("userName");
        String userToken = request.getHeader("userToken");

        if (StringUtils.isBlank(uid) || StringUtils.isBlank(userName) || StringUtils.isBlank(userToken)) {
            return false;
        }
        UserVo userVo = userService.getByToken(userToken);
        if (Objects.isNull(userVo)) {
            log.info("用户token失效！");
            return false;
        }

        String userType = StringUtils.isBlank(request.getHeader("userType")) ? "2" : request.getHeader("userType");

        //2. 判断DB中是否存在用户
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("uid", uid);
        User user = userService.getOne(userQueryWrapper);
        if (Objects.isNull(user)) {
            user = new User();
            user.setUid(uid);
            user.setCreateTime(LocalDateTime.now());
        }
        // 获取登陆人员信息
        Map<String, MatrixUserDetail> userMap = matrixService.getUserDetailByEmailPre(Arrays.asList(userName));
        MatrixUserDetail userDetail = userMap.get(userName);
        if (Objects.isNull(userDetail)) {
            throw new CustomException("登陆用户信息不正确！");
        }
        log.info("拦截器用户信息为：userName==={}，userDetail === {}", userName, JSON.toJSONString(userDetail));
        //3. 保存或更新用户
        if (StringUtils.isBlank(user.getRole())) {
            user.setRole(UserRoleEnum.ROLE_USER.getName());
        }
        user.setNickName(userDetail.getUserName());
        user.setTreePath(userDetail.getTreePath().substring(7).replace("-", ","));
        user.setEhrGroup(userDetail.getDeptName());

        user.setUserName(userName);
        user.setUpdateTime(LocalDateTime.now());
        user.setUserType(Integer.parseInt(userType));
        boolean flag = userService.saveOrUpdate(user);
        log.info("拦截器结束 ------------------------------------------------------ ");
        return flag;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


}
