package com.ziroom.qa.quality.defende.provider.mq;

import com.alibaba.fastjson.JSONPath;
import com.ziroom.qa.quality.defende.provider.entity.OmegaMqException;
import com.ziroom.qa.quality.defende.provider.entity.dto.KubeDeploymentDto;
import com.ziroom.qa.quality.defende.provider.service.IOmegaMqExceptionService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@Getter
@Setter
public class ConsumerService {

    @Value("${aims.consumerGroup}")
    private String consumerGroup;
    @Value("${aims.rocket.nameserver-addr}")
    private String namesrvAddr;
    @Value("${aims.id}")
    private String topic;

    @Autowired
    private IOmegaMqExceptionService omegaMqExceptionService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @PostConstruct
    public void defaultMQPushConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(topic, "OMEGA_DEPLOY_APP");
        consumer.setConsumeMessageBatchMaxSize(1);
//        默认就是集群模式
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                log.info(Thread.currentThread().getName() + " 开始接收RocketMq消息：");
                MessageExt msg = msgs.get(0);
                KubeDeploymentDto deploymentDto;
                try {
                    String raw = new String(msg.getBody(), StandardCharsets.UTF_8);
                    log.info("成功接受消息：{}", raw);
                    // 消息处理，组装对象为 KubeDeploymentDto
                    deploymentDto = getDto(raw);
                } catch (Exception e) {
                    log.error("消息解析失败, 消息为：{}", msg);
                    log.error("异常为", e);
                    //重试3次后落库
                    if (msg.getReconsumeTimes() >= 3) {
                        OmegaMqException entity = new OmegaMqException();
                        entity.setMsgId(msg.getMsgId());
                        String ext = "MessageExt [queueId=" + msg.getQueueId() + ", storeSize=" + msg.getStoreSize() + ", queueOffset=" + msg.getQueueOffset()
                                + ", sysFlag=" + msg.getSysFlag() + ", bornTimestamp=" + msg.getBornTimestamp() + ", bornHost=" + msg.getBornHost() + ", storeTimestamp="
                                + msg.getStoreTimestamp() + ", storeHost=" + msg.getStoreHost() + ", commitLogOffset=" + msg.getCommitLogOffset() + ", bodyCRC="
                                + msg.getBodyCRC() + ", reconsumeTimes=" + msg.getReconsumeTimes() + ", preparedTransactionOffset=" + msg.getPreparedTransactionOffset() + "]";
                        entity.setMsgExt(ext);
                        entity.setCreateTime(LocalDateTime.now());
                        omegaMqExceptionService.save(entity);
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    } else {
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                }
                DeploymentEvent event = new DeploymentEvent(this, deploymentDto);
                // 观察者设计模式，异步发布事件，运行服务、swagger 会监听该事件
                CompletableFuture.runAsync(()->{
                    eventPublisher.publishEvent(event);
                });
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        log.info("rocketMQ start");
    }



    // 通过从 rocketMQ 中转换出 KubeDeploymentDto 对象
    private KubeDeploymentDto getDto(String rawStr) {
        KubeDeploymentDto dto = new KubeDeploymentDto();
        // omega 应用名称
        dto.setAppName(JSONPath.read(rawStr, "$.app.applicationName").toString());
        // 命名空间，如 tech-daily
//        dto.setNameSpace(JSONPath.read(rawStr, "$.deployment.metadata.namespace").toString());
        // 接口任务集合自动运行用
        // 项目 id
//        dto.setProjectId(JSONPath.read(rawStr, "$.app.projectId").toString());
//        JSONObject labels = (JSONObject) JSONPath.read(rawStr, "$.deployment.metadata.labels");
        // 项目域名
//        dto.setEnvDomain(labels.getString("ziroom.com/domain"));
        // 是否 https
//        dto.setIsHttps(labels.getString("ziroom.com/https"));
        // 分支名称
        dto.setImageBranch(JSONPath.read(rawStr, "$.branch").toString());
//        应用类型
        dto.setSystemType(JSONPath.read(rawStr, "$.app.systemType").toString());
        return dto;
    }
}
