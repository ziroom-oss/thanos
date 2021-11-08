package com.ziroom.qa.quality.defende.provider.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.entity.TestMessage;
import com.ziroom.qa.quality.defende.provider.service.TestMessageService;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.TestMessageInfoVo;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Api(value = "用例消息中心", tags = {"用例消息中心"})
@Slf4j
@RestController
@RequestMapping("testMessage")
public class TestMessageController {
    @Autowired
    private TestMessageService testMessageService;

    @ApiOperation("获取用户未读取消息")
    @GetMapping("/viewMessage")
    public RestResultVo<TestMessageInfoVo> viewMessage(@RequestHeader String userName) {
        RestResultVo restResultVo;
        try {
            TestMessageInfoVo vo = testMessageService.findInfoByUserName(userName);
            restResultVo = RestResultVo.fromData(vo);
        } catch (Exception e) {
            log.error("testMessage/viewMessage userName=={} 获取用户未读取消息失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("用户读取消息")
    @GetMapping("/readMessage")
    public RestResultVo<String> readMessage(@RequestHeader String userName, @RequestParam("msgIdList") List<Long> msgIdList) {
        RestResultVo restResultVo;
        try {
            TestResultVo vo = testMessageService.batchReadMessage(msgIdList, userName);
            if (vo.getFlag()) {
                restResultVo = RestResultVo.fromSuccess(vo.getMsgRes());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(vo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("testMessage/readMessage userName=={} 用户读取消息失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("分页查询消息列表")
    @PostMapping(value = "/queryTestMessageByPage", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<Page<TestMessage>> queryTestMessageByPage(@RequestHeader String userName, @RequestBody Pagination<TestMessage> pagination) {
        RestResultVo restResultVo;
        try {
            Page<TestMessage> page = testMessageService.queryTestMessageByPage(pagination);
            pagination.setPage(page);
            restResultVo = RestResultVo.fromData(page);
        } catch (Exception e) {
            log.error("testMessage/queryTestMessageByPage userName=={} 获取用户消息集合失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("新增消息")
    @PostMapping("/insertNewMessage")
    public RestResultVo<String> insertNewMessage(@RequestHeader String userName, @RequestBody TestMessage testMessage) {
        RestResultVo restResultVo;
        try {
            testMessage.setCreateUser(userName);
            testMessage.setCreateTime(LocalDateTime.now());
            boolean flag = testMessageService.insertTestMessage(testMessage);
            if (flag) {
                restResultVo = RestResultVo.fromData(null);
            } else {
                restResultVo = RestResultVo.fromErrorMessage("新增失败");
            }
        } catch (Exception e) {
            log.error("testMessage/insertNewMessage userName=={} 新增消息失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("修改消息")
    @PostMapping("/updateNewMessage")
    public RestResultVo<String> updateNewMessage(@RequestHeader String userName, @RequestBody TestMessage testMessage) {
        RestResultVo restResultVo;
        try {
            testMessage.setUpdateUser(userName);
            testMessage.setUpdateTime(LocalDateTime.now());
            boolean flag = testMessageService.updateTestMessage(testMessage);
            if (flag) {
                restResultVo = RestResultVo.fromData("成功");
            } else {
                restResultVo = RestResultVo.fromErrorMessage("更新失败");
            }
        } catch (Exception e) {
            log.error("testMessage/updateNewMessage userName=={} 修改消息失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("删除消息")
    @GetMapping("/deleteNewMessage")
    public RestResultVo<String> deleteNewMessage(@RequestHeader String userName, @RequestParam("msgIdList") List<Long> msgIdList) {
        RestResultVo restResultVo;
        try {
            TestResultVo vo = testMessageService.deleteTestMessage(msgIdList);
            if (vo.getFlag()) {
                restResultVo = RestResultVo.fromSuccess(vo.getMsgRes());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(vo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("testMessage/deleteNewMessage userName=={} 删除消息失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("判断标题是否相同")
    @GetMapping("/checkNewMessageTitle")
    public RestResultVo<String> checkNewMessageTitle(@RequestHeader String userName,
                                                     @RequestParam(value = "msgId", required = false) Long msgId,
                                                     @RequestParam("msgTitle") String msgTitle) {
        RestResultVo restResultVo;
        try {
            TestResultVo vo = testMessageService.checkNewMessageTitle(msgId, msgTitle);
            if (vo.getFlag()) {
                restResultVo = RestResultVo.fromSuccess(vo.getMsgRes());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(vo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("testMessage/checkNewMessageTitle userName=={} 判断标题是否相同失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("根据msgId获取消息详情")
    @GetMapping("/findMessageById")
    public RestResultVo<TestMessage> findMessageById(@RequestHeader String userName,
                                                     @RequestParam(value = "msgId") Long msgId) {
        RestResultVo restResultVo;
        try {
            TestMessage message = testMessageService.getById(msgId);
            restResultVo = RestResultVo.fromData(message);
        } catch (Exception e) {
            log.error("testMessage/findMessageById msgId=={} 根据id获取消息信息失败!", msgId, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

}
