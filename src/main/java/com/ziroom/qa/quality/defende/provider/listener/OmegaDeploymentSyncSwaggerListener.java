package com.ziroom.qa.quality.defende.provider.listener;

import com.alibaba.fastjson.JSON;
import com.ziroom.qa.quality.defende.provider.caseRepository.service.IAutoApplicationPropertiesService;
import com.ziroom.qa.quality.defende.provider.entity.dto.KubeDeploymentDto;
import com.ziroom.qa.quality.defende.provider.mq.DeploymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.Resource;

/**
 *
 */
@Slf4j
@Configuration
@EnableAsync
public class OmegaDeploymentSyncSwaggerListener{
    @Resource
    private IAutoApplicationPropertiesService autoApplicationPropertiesService;
    @EventListener(condition = "#deploymentEvent.deployment.imageBranch.contains('qua')")
    @Async
    public void listen(DeploymentEvent deploymentEvent) {
        KubeDeploymentDto dto = deploymentEvent.getDeployment();
        log.info("omega项目部署成功后MQ消息[{}],触发swagger同步接口",JSON.toJSONString(dto));
        // CD部署完成后，自动运行项目冒烟/回归类型接口任务集合
        String branch = dto.getImageBranch();
        String systemType = dto.getSystemType();

//        运行条件。qua环境
        if(systemType!=null&&!systemType.contains("vue")){
//        omega的原因，需要等流量且过去
            log.info("为了响应运维切换路由，thread sleep 40s");
            try {
                Thread.sleep(40000);
            } catch (InterruptedException e) {
                log.error("为了响应运维切换路由，thread 40s",e);
            }
            log.info("sleep 结束 ，开始回归测试！");
            autoApplicationPropertiesService.synchronizeApiByApp(dto.getAppName(),"qua");
        }

    }
}
