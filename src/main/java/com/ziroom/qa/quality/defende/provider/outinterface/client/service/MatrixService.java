package com.ziroom.qa.quality.defende.provider.outinterface.client.service;


import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.ziroom.qa.quality.defende.provider.entity.User;
import com.ziroom.qa.quality.defende.provider.service.UserService;
import com.ziroom.qa.quality.defende.provider.util.RedisUtil;
import com.ziroom.qa.quality.defende.provider.vo.EhrUserDetail;
import com.ziroom.qa.quality.defende.provider.vo.MatrixUserDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MatrixService {

    @Autowired
    private RedisUtil redisUtil;
    @Value("${ziroom.ehr.defendeRediskey}")
    private String redisKey = "quality-defende-InternetPlatEhrUserList";

    private static final long redisTimeOutHours = 12;

    @Autowired
    private UserService userService;

    public List<EhrUserDetail> getInternetPlatEhrUserListFromMatrix(String deptCode) {
        //1. 声明返回值

        List<EhrUserDetail> ehrUserDetailList = Lists.newArrayList();
        List<User> userList = userService.getUserListByDeptCode(deptCode);
        if (CollectionUtils.isNotEmpty(userList)) {
            for (User user : userList) {
                EhrUserDetail ehrUserDetail = new EhrUserDetail();
                ehrUserDetail.setName(user.getNickName());//中文名称
                ehrUserDetail.setAdCode(user.getUserName());//邮箱前缀
                ehrUserDetail.setEmplid(user.getUid());//员工工号
                ehrUserDetail.setUserType(user.getUserType() + "");//类型 1 正式 2 外包
                ehrUserDetailList.add(ehrUserDetail);
            }
        }
        return ehrUserDetailList;
    }

    public List<EhrUserDetail> getMatrixUserRedisInfo(String deptCode) {
        List<EhrUserDetail> ehrUserDetailList;
        if (Objects.nonNull(redisUtil.get(redisKey))) {
            ehrUserDetailList = JSONArray.parseArray(redisUtil.get(redisKey).toString(), EhrUserDetail.class);
            if (CollectionUtils.isNotEmpty(ehrUserDetailList)) {
                return ehrUserDetailList;
            }
        }
        ehrUserDetailList = this.getInternetPlatEhrUserListFromMatrix(deptCode);
        redisUtil.setFewHour(redisKey, ehrUserDetailList, redisTimeOutHours);
        return ehrUserDetailList;
    }

    public void saveInternetPlatEhrUserListToRedis(String deptCode) {
        List<EhrUserDetail> ehrUserDetailList = this.getInternetPlatEhrUserListFromMatrix(deptCode);
        redisUtil.setFewHour(redisKey, ehrUserDetailList, redisTimeOutHours);
    }

    /**
     * 根据邮箱前缀集合获取用户信息
     *
     * @param emailPreList
     * @return
     */
    public Map<String, MatrixUserDetail> getUserDetailByEmailPre(List<String> emailPreList) {
        if (CollectionUtils.isEmpty(emailPreList)) {
            return null;
        }
        Map<String, MatrixUserDetail> map = null;
        List<User> userList = userService.getUserListByUserNames(emailPreList);
        if (CollectionUtils.isNotEmpty(userList)) {
            List<MatrixUserDetail> matrixUserList = new ArrayList<>();
            userList.stream().forEach(user -> {
                MatrixUserDetail matrixUser = new MatrixUserDetail();
                matrixUser.setUserCode(user.getUserName());
                matrixUser.setUserName(user.getNickName());
                matrixUser.setDeptCode(user.getTreePath());
                matrixUser.setDeptName(user.getEhrGroup());
                matrixUser.setUserType(user.getUserType() + "");//类型 1 正式 2 外包
                matrixUserList.add(matrixUser);
            });
            map = matrixUserList.stream().collect(Collectors.toMap(MatrixUserDetail::getUserCode, Function.identity()));
        }
        return map;
    }

    /**
     * 根据用户名称获取用户信息集合（like查询）
     *
     * @param userName
     * @return
     */
    public List<EhrUserDetail> getEhrUserDetailLikeUserName(String userName) {
        List<EhrUserDetail> ehrUserDetailList = new ArrayList<>();

        List<User> userList = userService.getUserInfoLikeUserName(userName);
        if (CollectionUtils.isEmpty(userList)) {
            return ehrUserDetailList;
        }
        userList.stream().forEach(user -> {
            EhrUserDetail ehrUserDetail = new EhrUserDetail();
            ehrUserDetail.setName(user.getNickName());//中文名称
            ehrUserDetail.setAdCode(user.getUserName());//邮箱前缀
            ehrUserDetail.setEmplid(user.getUid());//员工工号
            ehrUserDetail.setUserType(user.getUserType() + "");//类型 1 正式 2 外包
            ehrUserDetailList.add(ehrUserDetail);
        });
        return ehrUserDetailList;
    }
}
