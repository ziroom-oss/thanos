package com.ziroom.qa.quality.defende.provider.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ziroom.qa.quality.defende.provider.config.OperateLogAnnotation;
import com.ziroom.qa.quality.defende.provider.constant.OperateLogModuleConstants;
import com.ziroom.qa.quality.defende.provider.constant.OperateLogTypeConstants;
import com.ziroom.qa.quality.defende.provider.constant.enums.TestPlanStatusEnum;
import com.ziroom.qa.quality.defende.provider.entity.TestPlan;
import com.ziroom.qa.quality.defende.provider.service.TestPlanService;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author liangh4
 * @since 2021-04-26
 */
@Api(value = "测试计划管理", tags = {"测试计划管理"})
@Slf4j
@RestController
@RequestMapping("/testPlan")
public class TestPlanController {

    @Autowired
    private TestPlanService testPlanService;

    @ApiOperation("分页查询用例计划列表")
    @PostMapping(value = "/queryTestPlanByPage", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<Page<TestPlan>> queryTestPlanByPage(@RequestHeader(name = "userName") String userName, @RequestBody Pagination<TestPlan> pagination) {
        RestResultVo restResultVo;
        try {
            Page<TestPlan> page = testPlanService.queryTestPlanByPage(pagination);
            restResultVo = RestResultVo.fromData(page);
        } catch (Exception e) {
            log.error("/testPlan/queryTestPlanByPage uid=={} 获取用户用力计划集合失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("新增用例计划")
    @PostMapping("/insertTestPlan")
    public RestResultVo<String> insertTestPlan(@RequestHeader(name = "userName") String userName, @RequestBody TestPlan testPlan) {
        RestResultVo restResultVo;
        try {
            testPlan.setCreateUser(userName);
            testPlan.setCreateTime(LocalDateTime.now());
            testPlan.setUpdateUser(userName);
            testPlan.setUpdateTime(LocalDateTime.now());

            TestResultVo vo = testPlanService.insertTestPlan(testPlan);

            if (vo.getFlag()) {
                restResultVo = RestResultVo.fromData(vo.getFlag());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(vo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/testPlan/insertTestPlan uid=={} 新增用力计划失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("提交保存用例计划")
    @GetMapping("/submitTestPlanList")
    public RestResultVo<String> submitTestPlanList(@RequestHeader(name = "userName") String userName, @RequestParam("idList") List<Long> idList) {
        RestResultVo restResultVo;
        try {
            TestResultVo vo = testPlanService.submitTestPlanList(userName, idList);
            if (vo.getFlag()) {
                restResultVo = RestResultVo.fromData(vo.getFlag());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(vo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/testPlan/updateTestPlan uid=={} 修改用力计划失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("修改用例计划")
    @PostMapping("/updateTestPlan")
    public RestResultVo<String> updateTestPlan(@RequestHeader(name = "userName") String userName, @RequestBody TestPlan testPlan) {
        RestResultVo restResultVo;
        try {
            testPlan.setUpdateUser(userName);
            testPlan.setUpdateTime(LocalDateTime.now());
            TestResultVo vo = testPlanService.updateTestPlan(testPlan);
            if (vo.getFlag()) {
                restResultVo = RestResultVo.fromData(vo.getFlag());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(vo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/testPlan/updateTestPlan uid=={} 修改用力计划失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("删除用例计划")
    @GetMapping("/deleteTestPlan")
    public RestResultVo<String> deleteTestPlan(@RequestHeader(name = "userName") String userName, @RequestParam("idList") List<Long> idList) {
        RestResultVo restResultVo;
        try {
            TestResultVo vo = testPlanService.deleteTestPlan(userName, idList);
            if (vo.getFlag()) {
                restResultVo = RestResultVo.fromData(vo.getFlag());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(vo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/testPlan/deleteTestPlan uid=={} 删除用力计划失败!", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("判断用例计划名称是否相同")
    @GetMapping("/checkNewPlanName")
    public RestResultVo<String> checkNewPlanName(@RequestParam(value = "id", required = false) Long id, @RequestParam("planName") String planName) {
        RestResultVo restResultVo;
        try {
            TestResultVo vo = testPlanService.checkNewPlanName(id, planName);
            if (vo.getFlag()) {
                restResultVo = RestResultVo.fromData(vo.getFlag());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(vo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/testPlan/checkNewPlanName planName=={} 判断用例计划名称是否相同失败!", planName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("根据id获取用例计划详情")
    @GetMapping("/findPlanInfoById")
    public RestResultVo<TestPlan> findPlanInfoById(@RequestParam(value = "id") Long id) {
        RestResultVo restResultVo;
        try {
            TestPlan plan = testPlanService.getTestPlanById(id);
            restResultVo = RestResultVo.fromData(plan);
        } catch (Exception e) {
            log.error("/testPlan/findPlanInfoById id=={} 根据id获取用例计划详情失败!", id, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("查询待审核的测试计划Id集合")
    @PostMapping(value = "/queryIdsByUncheck", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<List<Long>> queryIdsByUncheck(@RequestBody TestPlan testPlan) {
        RestResultVo restResultVo;
        try {
            // 设置待审核状态条件
            testPlan.setTestPlanStatus(TestPlanStatusEnum.PENDING.getTestPlanStatus());
            testPlan.setDeleteFlag(false);
            testPlan.setLatestFlag(true);
            List<Long> idList = testPlanService.queryIdsByUncheck(testPlan);
            restResultVo = RestResultVo.fromData(idList);
        } catch (Exception e) {
            log.error("/testPlan/queryIdsByUncheck testPlan=={} 查询待审核的测试计划Id集合失败!", testPlan, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("查询用例计划历史版本")
    @GetMapping(value = "/queryTestPlanHistoryByPlanKey")
    public RestResultVo<List<TestPlan>> queryTestPlanHistoryByPlanKey(@RequestParam String planKey) {
        RestResultVo restResultVo;
        try {
            List<TestPlan> list = testPlanService.queryTestPlanHistoryByPlanKey(planKey);
            restResultVo = RestResultVo.fromData(list);
        } catch (Exception e) {
            log.error("/testPlan/queryTestPlanHistoryByPlanKey planKey=={} 查询用例计划历史版本失败!", planKey, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("批量审批测试用例计划")
    @GetMapping(value = "/batchApprovedTestPlan", produces = {"application/json;charset=UTF-8"})
    public RestResultVo<String> batchApprovedTestPlan(@RequestParam List<Long> idList,
                                                      @RequestParam(name = "approve") Integer approve,
                                                      @RequestParam(name = "remark") String remark,
                                                      HttpServletRequest request) {
        String userName = request.getHeader("userName");
        RestResultVo restResultVo;
        try {
            TestResultVo resultVo = testPlanService.batchApprovedTestPlan(userName, approve, remark, idList);
            if (resultVo.getFlag()) {
                restResultVo = RestResultVo.fromSuccess(resultVo.getMsgRes());
            } else {
                restResultVo = RestResultVo.fromErrorMessage(resultVo.getMsgRes());
            }
        } catch (Exception e) {
            log.error("/testPlan/checkNewPlanName idList=={} 批量审批测试用例计划失败!", idList, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

}

