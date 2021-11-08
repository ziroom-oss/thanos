package com.ziroom.qa.quality.defende.provider.mq;

import com.ziroom.qa.quality.defende.provider.entity.dto.KubeDeploymentDto;
import org.springframework.context.ApplicationEvent;

/**
 * @Author: yinm5
 * @Description:
 * @Date: 11:07 2020/1/9
 */
public class DeploymentEvent extends ApplicationEvent {

    private KubeDeploymentDto deployment;

    public DeploymentEvent(Object source, KubeDeploymentDto dto) {
        super(source);
        this.deployment = dto;
    }

    public KubeDeploymentDto getDeployment() {
        return deployment;
    }
}
