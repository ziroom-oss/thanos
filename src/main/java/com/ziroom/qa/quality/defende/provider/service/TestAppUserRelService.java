package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.TestAppUserRel;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;

import java.util.List;

/**
 * <p>
 * 应用用户关联 服务类
 * </p>
 *
 * @author liangh4
 * @since 2021-07-19
 */
public interface TestAppUserRelService extends IService<TestAppUserRel> {

    /**
     * 关注应用
     *
     * @param appId
     * @param emailPre
     * @return
     */
    TestResultVo followApplication(Long appId, String emailPre);

    /**
     * 取消关注应用
     *
     * @param appId
     * @param emailPre
     * @return
     */
    TestResultVo unfollowApplication(Long appId, String emailPre);

    /**
     * 批量关注
     *
     * @param appIdList
     * @param emailPre
     * @return
     */
    TestResultVo batchFollowApplication(List<Long> appIdList, String emailPre);

    /**
     * 根据邮箱前缀查询所有应用集合
     *
     * @param emailPre
     * @return
     */
    List<Long> findFollowAppByEmailPre(String emailPre);

}
