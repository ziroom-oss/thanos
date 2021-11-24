package com.ziroom.qa.quality.defende.provider.outinterface.client.service;

import com.ziroom.qa.quality.defende.provider.properties.WeChatProperties;
import com.ziroom.qa.quality.defende.provider.vo.outvo.SendTaskMsgVo;
import com.ziroom.qa.quality.defende.provider.vo.wechat.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WeChatService {

    @Autowired
    private WeChatProperties weChatProperties;
    @Value("${quality.baseurl}")
    private String qualityUrl;

    public void sendTestTaskMsg(SendTaskMsgVo sendTaskMsgVo) {
        StringBuilder sb = new StringBuilder();
        sb.append("您好，测试执行：" + sendTaskMsgVo.getTaskName() + "，已经创建完成\n");
        sb.append(">需求JIRA：" + sendTaskMsgVo.getJiraUrl() + "\n");
        sb.append(">关联分支：" + sendTaskMsgVo.getBranchName() + "\n");
        sb.append(">测试执行地址：" + sendTaskMsgVo.getTestTaskUrl() + "\n");
        sb.append(">开发负责人：" + sendTaskMsgVo.getDevUserNameStr() + "\n");
        sb.append(">测试负责人：" + sendTaskMsgVo.getTestUserNameStr() + "\n");
        if (StringUtils.isNotBlank(sendTaskMsgVo.getTestCaseResult())) {
            sb.append(">测试用例关联结果：" + sendTaskMsgVo.getTestCaseResult() + "\n");
        }
        sb.append(">[点击查看详情](" + sendTaskMsgVo.getTestTaskUrl() + ")");

        List<String> userNameList = Arrays.asList(sendTaskMsgVo.getDevUserName(), sendTaskMsgVo.getTestUserName()).stream().distinct().collect(Collectors.toList());
        this.sendMarkdown(userNameList, sb.toString());
    }

    public void sendAutoCountMsg(SendTaskMsgVo sendTaskMsgVo) {

        String format = String.format("自动化测试使用情况\n" +
                "                              ><font color=\"info\">用例数(已执行):</font>\n%s" +
                "                              >\n" +
                "                              ><font color=\"info\">执行成功率:</font>\n%s" +
                "                              >\n" +
                "                              ><font color=\"info\">昨日新增用例数:</font>\n%s" +
                "                              >\n" +
                "                              ><font color=\"info\">测试执行数:</font>\n%s" +
                "                              >\n" +
                "                              >[点击添加用例](%s)", sendTaskMsgVo.getExeCaseStr(), sendTaskMsgVo.getTaskRateStr(), sendTaskMsgVo.getCreCaseStr(), sendTaskMsgVo.getTaskCountStr(), qualityUrl);
        this.sendDeptRankRobotMsg(format);
    }


    /**
     * @param list
     * @param content 参考文档 https://open.work.weixin.qq.com/api/doc/90000/90135/90236#%E6%94%AF%E6%8C%81%E7%9A%84markdown%E8%AF%AD%E6%B3%95
     *                您的会议室已经预定，稍后会同步到`邮箱`
     *                >**事项详情**
     *                >事　项：<font color=\"info\">开会</font>
     *                >组织者：@miglioguan
     *                >参与者：@miglioguan、@kunliu、@jamdeezhou、@kanexiong、@kisonwang
     *                >
     *                >会议室：<font color=\"info\">广州TIT 1楼 301</font>
     *                >日　期：<font color=\"warning\">2018年5月18日</font>
     *                >时　间：<font color=\"comment\">上午9:00-11:00</font>
     *                >
     *                >请准时参加会议。
     *                >
     *                >如需修改会议信息，请点击：[修改会议信息](https://work.weixin.qq.com)
     */
    public void sendMarkdown(List<String> list, String content) {
        Message messageBody = new Message();
        StringBuffer stringBuffer = new StringBuffer();
        list.forEach(user -> {
            stringBuffer.append(user).append("|");
        });
        String s = stringBuffer.toString();
        if (s.contains("|"))
            s = s.substring(0, s.length() - 1);
        messageBody.setToUser(s);
        messageBody.setMsgType("markdown");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("content", content);
        messageBody.setParams(hashMap);
        messageBody.setPushType("app");
        this.sendMsg(messageBody);
    }

    public void sendDeptRankRobotMsg(String content) {
        Message messageBody = new Message();
        messageBody.setRobotKey(weChatProperties.getRobotKey());
        messageBody.setMsgType("markdown");
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put("content", content);
        messageBody.setParams(params);
        messageBody.setPushType("group_chat_robot");
        this.sendMsg(messageBody);
    }


    private void sendMsg(Message message) {
        message.setToken(weChatProperties.getToken());
        message.setModelCode(weChatProperties.getTemplateId());
        log.info("message[{}]", message);
//        WechatResult result = weChatRobotApi.sendWeChatMsg(message);
//        log.info("{}-{}", result.getCode(), result.getMessage());
    }

}
