package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.constant.TestCenterConstants;
import com.ziroom.qa.quality.defende.provider.entity.TestAppUserRel;
import com.ziroom.qa.quality.defende.provider.mapper.TestAppUserRelMapper;
import com.ziroom.qa.quality.defende.provider.service.TestAppUserRelService;
import com.ziroom.qa.quality.defende.provider.vo.TestResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 应用用户关联 服务实现类
 * </p>
 *
 * @author liangh4
 * @since 2021-07-19
 */
@Service
@Slf4j
public class TestAppUserRelServiceImpl extends ServiceImpl<TestAppUserRelMapper, TestAppUserRel> implements TestAppUserRelService {

    /**
     * 关注应用
     *
     * @param appId
     * @param emailPre
     * @return
     */
    @Override
    public TestResultVo followApplication(Long appId, String emailPre) {
        if (Objects.isNull(appId) || StringUtils.isBlank(emailPre)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        List<TestAppUserRel> list = this.findByAppAndEmailPre(Arrays.asList(appId), emailPre);
        if (CollectionUtils.isEmpty(list)) {
            TestAppUserRel rel = new TestAppUserRel();
            rel.setEmailPre(emailPre);
            rel.setAppId(appId);
            rel.setCreateUser(emailPre);
            rel.setUpdateUser(emailPre);
            rel.setCreateTime(LocalDateTime.now());
            rel.setUpdateTime(LocalDateTime.now());
            super.save(rel);
        }
        return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).build();
    }

    /**
     * 取消关注应用
     *
     * @param appId
     * @param emailPre
     * @return
     */
    @Override
    public TestResultVo unfollowApplication(Long appId, String emailPre) {
        if (Objects.isNull(appId) || StringUtils.isBlank(emailPre)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        List<TestAppUserRel> list = this.findByAppAndEmailPre(Arrays.asList(appId), emailPre);
        if (CollectionUtils.isNotEmpty(list)) {
            super.removeByIds(list.stream().map(TestAppUserRel::getId).collect(Collectors.toList()));
        }
        return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).build();
    }

    /**
     * 批量关注
     *
     * @param appIdList
     * @param emailPre
     * @return
     */
    @Override
    public TestResultVo batchFollowApplication(List<Long> appIdList, String emailPre) {
        if (CollectionUtils.isEmpty(appIdList) || StringUtils.isBlank(emailPre)) {
            return TestResultVo.builder().flag(false).msgRes(TestCenterConstants.RES_MSG_PARAMS_EMPTY).build();
        }
        List<TestAppUserRel> list = this.findByAppAndEmailPre(appIdList, emailPre);
        if (CollectionUtils.isNotEmpty(list)) {
            super.removeByIds(list.stream().map(TestAppUserRel::getId).collect(Collectors.toList()));
        }
        List<TestAppUserRel> newList = new ArrayList<>();
        for (Long appId : appIdList) {
            TestAppUserRel rel = new TestAppUserRel();
            rel.setEmailPre(emailPre);
            rel.setAppId(appId);
            rel.setCreateUser(emailPre);
            rel.setUpdateUser(emailPre);
            rel.setCreateTime(LocalDateTime.now());
            rel.setUpdateTime(LocalDateTime.now());
            newList.add(rel);
        }
        super.saveBatch(newList);
        return TestResultVo.builder().flag(true).msgRes(TestCenterConstants.RES_MSG_SUCCESS).build();
    }

    /**
     * 根据邮箱前缀查询所有应用集合
     *
     * @param emailPre
     * @return
     */
    @Override
    public List<Long> findFollowAppByEmailPre(String emailPre) {
        List<TestAppUserRel> list = this.findByAppAndEmailPre(null, emailPre);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.stream().map(TestAppUserRel::getAppId).collect(Collectors.toList());
    }

    /**
     * 查询关注的信息集合
     *
     * @param appIdList
     * @param emailPre
     * @return
     */
    private List<TestAppUserRel> findByAppAndEmailPre(List<Long> appIdList, String emailPre) {
        QueryWrapper<TestAppUserRel> queryWrapper = new QueryWrapper();
        queryWrapper.in(CollectionUtils.isNotEmpty(appIdList), "app_id", appIdList);
        queryWrapper.eq("email_pre", emailPre);
        return super.list(queryWrapper);
    }
}
