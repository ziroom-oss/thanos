package com.ziroom.qa.quality.defende.provider.aop;

import com.alibaba.fastjson.JSON;
import com.ziroom.qa.quality.defende.provider.config.OperateLogAnnotation;
import com.ziroom.qa.quality.defende.provider.entity.Monitoring;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.EhrService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.MatrixService;
import com.ziroom.qa.quality.defende.provider.service.MonitoringService;
import com.ziroom.qa.quality.defende.provider.vo.EhrUserDetail;
import com.ziroom.qa.quality.defende.provider.vo.OutsourcingPersonnelVo;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.time.LocalDateTime;


@Aspect
@Component
public class SysLogAspect {

    Logger logger = LoggerFactory.getLogger(SysLogAspect.class);

    @Autowired
    private EhrService ehrService;
    @Autowired
    private MonitoringService monitoringService;
    @Autowired
    private MatrixService matrixService;

    @ApiOperation("定义切点 @Pointcut | 在注解的位置切入代码")
    @Pointcut("@annotation(com.ziroom.qa.quality.defende.provider.config.OperateLogAnnotation)")
    public void logPointCut() {
    }

    @ApiOperation("切面配置通知")
    @Around("logPointCut()")
    public Object saveSysLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Monitoring sysLog = new Monitoring();
        Object result = proceedingJoinPoint.proceed();
        try {

            //1. 从Header中获取所有的用户信息
            String uid = request.getHeader("uid");
            String userName = request.getHeader("userName");
            String nickName = URLDecoder.decode(request.getHeader("nickName"),"utf-8");
            String userType = StringUtils.isBlank(request.getHeader("userType")) ? "2" : request.getHeader("userType");

            //2. 开始时间
            sysLog.setExeStartTime(LocalDateTime.now());

            //3.从切面织入点获取方法
            MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
            Method method = signature.getMethod();

            //4.获取注解操作
            OperateLogAnnotation operateLog = method.getAnnotation(OperateLogAnnotation.class);
            if (null != operateLog) {
                String option = operateLog.option();
                sysLog.setType(option);
                String module = operateLog.moduleName();
                sysLog.setMonitorInfo(module);
            }

            //5.获取请求的类名
            String className = proceedingJoinPoint.getTarget().getClass().getName();
            String methodName = method.getName();
            sysLog.setDescript(className + "." + methodName);

            //6.请求的参数
            String params = JSON.toJSONString(proceedingJoinPoint.getArgs());
            sysLog.setParams(params);

            //7. 用户信息
            sysLog.setOpTime(LocalDateTime.now());
            sysLog.setUserCode(userName);
            sysLog.setOpPerson(nickName);
            sysLog.setOpPersonUserName(userName);
            String treePath = "";
            if(String.valueOf(2).equals(userType)){//正式员工
                RestResultVo<EhrUserDetail> ehrUserDetailRestResultVo = ehrService.getUserDetailFromEhr(uid);
                EhrUserDetail ehrUserDetail = ehrUserDetailRestResultVo.getData();
                treePath = ehrUserDetail.getTreePath();
            }else if (String.valueOf(3).equals(userType)){//外包人员
                OutsourcingPersonnelVo outsourcingPersonnelVo = matrixService.queryOutsourcingPersonnelFromMatrix(userName);
                treePath = outsourcingPersonnelVo.getEhrTreePath();
            }
            sysLog.setDeptEhrCode(treePath);

            //8.获取用户ip地址
            String clientIp = request.getRemoteAddr();
            sysLog.setClientIp(clientIp);
            sysLog.setClientIpReqTime(LocalDateTime.now());

            //9.获取服务ip地址
            String serverIp = request.getLocalAddr();
            sysLog.setServerIp(serverIp);
            sysLog.setServerIpReqTime(LocalDateTime.now());

            //10. 请求URI
            sysLog.setReqUrl(request.getRequestURL().toString());
            sysLog.setExeEndTime(LocalDateTime.now());
            sysLog.setExeStatus(0);

        } catch (Throwable throwable) {
            //异常状态
            sysLog.setExeStatus(1);
            //出现异常状态或者时间大于3s插入高危库
            logger.info(throwable.getMessage());
        } finally {
            monitoringService.save(sysLog);
            return result;
        }
    }

    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void insertAfterThrowing(JoinPoint point, Throwable e) {
    }

}
