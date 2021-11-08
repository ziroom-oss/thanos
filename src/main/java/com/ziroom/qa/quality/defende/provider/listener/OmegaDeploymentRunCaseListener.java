package com.ziroom.qa.quality.defende.provider.listener;

import com.alibaba.fastjson.JSON;
import com.ziroom.qa.quality.defende.provider.entity.dto.KubeDeploymentDto;
import com.ziroom.qa.quality.defende.provider.execTask.service.IAutoTestCaseExecuteService;
import com.ziroom.qa.quality.defende.provider.mq.DeploymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *
 */
@Slf4j
@Component
@EnableAsync
public class OmegaDeploymentRunCaseListener{
    @Resource
    private IAutoTestCaseExecuteService autoTestCaseExecuteService;

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    @EventListener(condition = "#deploymentEvent.deployment.imageBranch.contains('qua')")
    @Async
    public void listen(DeploymentEvent deploymentEvent) {

        KubeDeploymentDto dto = deploymentEvent.getDeployment();
        log.info("omega项目部署成功后MQ消息[{}],触发自动化回归测试",JSON.toJSONString(dto));

        // CD部署完成后，自动运行项目冒烟/回归类型接口任务集合
        String ktDomain = Boolean.valueOf(dto.getIsHttps()) ? HTTPS + dto.getEnvDomain() : HTTP + dto.getEnvDomain();
        String branch = dto.getImageBranch();
        String systemType = dto.getSystemType();

//        运行条件。qua环境
        if(branch.contains("qua")&&(systemType!=null&&!systemType.contains("vue"))){
//        omega的原因，需要等流量且过去
            log.info("为了响应运维切换路由，thread 60s");
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                log.info("为了响应运维切换路由，thread 60s");
            }
            log.info("sleep 结束 ，开始回归测试！");
            autoTestCaseExecuteService.runAllCaseByAppName("qua", dto.getAppName(),branch);
        }
    }
}
