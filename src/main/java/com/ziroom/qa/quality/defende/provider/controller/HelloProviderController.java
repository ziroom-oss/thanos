package com.ziroom.qa.quality.defende.provider.controller;

import com.ziroom.qa.quality.defende.provider.entity.dto.KubeDeploymentDto;
import com.ziroom.qa.quality.defende.provider.mq.DeploymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HelloProviderController  {
    @Autowired
    ApplicationEventPublisher eventPublisher;
    @GetMapping("/helloProvider")
    public String helloProvider(String env,String applicationName) {
        log.info("hello provider");
        test(env,applicationName);
        return "hello provider";
    }
    private void test(String env,String applicationName) {
        KubeDeploymentDto deploymentDto =new KubeDeploymentDto();
//        deploymentDto.setAppName("swx-api");
        deploymentDto.setAppName(applicationName);
        deploymentDto.setSystemType("java");
        deploymentDto.setImageBranch(env);
        DeploymentEvent event = new DeploymentEvent(this, deploymentDto);
        // 观察者设计模式，异步发布事件，运行服务、swagger 会监听该事件
//        CompletableFuture.runAsync(()->{
        eventPublisher.publishEvent(event);
//        });
    }
}
