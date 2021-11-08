package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.TestPlan;
import com.ziroom.qa.quality.defende.provider.vo.Pagination;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liangh4
 * @since 2021-04-26
 */
public interface TestPlanService extends IService<TestPlan> {

    /**
     * 分页查询用例计划
     * @param pagination
     * @return
     */
    Page<TestPlan> queryTestPlanByPage(Pagination<TestPlan> pagination);

    /**
     * 修改用例计划
     * @param testPlan
     * @return
     */
    TestResultVo updateTestPlan(TestPlan testPlan);

    /**
     * 新增用例计划
     * @param testPlan
     * @return
     */
    TestResultVo insertTestPlan(TestPlan testPlan);

    /**
     * 批量删除用例计划
     * @param uid
     * @param idList
     * @return flag:true 通过 false 不通过
     *         msgRes: 原因
     */
    TestResultVo deleteTestPlan(String uid,List<Long> idList);

    /**
     * 校验用例计划名称是否重复
     * @param id
     * @param planName
     * @return flag:true 通过 false 不通过
     *         msgRes: 原因
     */
    TestResultVo checkNewPlanName(Long id, String planName);

    /**
     * 获取未审核的测试计划id集合
     * @param testPlan 查询条件
     * @return
     */
    List<Long> queryIdsByUncheck(TestPlan testPlan);

    /**
     * 根据key获取用例计划历史
     * @param planKey
     * @return
     */
    List<TestPlan> queryTestPlanHistoryByPlanKey(String planKey);

    /**
     * 批量审核
     * @param userName 用户id
     * @param approve 审核状态
     * @param remark 备注
     * @param idList 批量审核id集合
     * @return
     */
    TestResultVo batchApprovedTestPlan(String userName, Integer approve, String remark, List<Long> idList);

    /**
     * 提交保存（更改执行计划的状态为待审核）
     * @param userName 用户id
     * @param idList 测试计划的id集合
     * @return
     */
    TestResultVo submitTestPlanList(String userName, List<Long> idList);

    /**
     * 根据id获取用例计划
     * @param id
     * @return
     */
    TestPlan getTestPlanById(Long id);
}
