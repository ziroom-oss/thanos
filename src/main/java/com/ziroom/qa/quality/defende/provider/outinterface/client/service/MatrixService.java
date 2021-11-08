package com.ziroom.qa.quality.defende.provider.outinterface.client.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.ziroom.qa.quality.defende.provider.entity.User;
import com.ziroom.qa.quality.defende.provider.outinterface.client.MatrixApiClient;
import com.ziroom.qa.quality.defende.provider.service.UserService;
import com.ziroom.qa.quality.defende.provider.util.RedisUtil;
import com.ziroom.qa.quality.defende.provider.vo.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class MatrixService {

    @Autowired
    private MatrixApiClient matrixApiClient;
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
        long start = System.currentTimeMillis();
        //2. 调用矩阵平台获取互联网团队的所有的用户
        EhrUserSearchDetailDto ehrUserSearchDetailDto = new EhrUserSearchDetailDto();
        ehrUserSearchDetailDto.setDeptCode(deptCode);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(ehrUserSearchDetailDto));
        JSONObject result = matrixApiClient.getUserDetailBySearchInfoFromMatrix(body);
        EhrUserResultDto ehrUserResult = result.getJSONObject("data").toJavaObject(EhrUserResultDto.class);
        List<EhrUserSearchDetailDto> userList = ehrUserResult.getUserList();
        log.info("从矩阵平台获取用户的总数:{}", userList.size());
        HashSet<EhrUserSearchDetailDto> userSet = new HashSet<>(userList);
        for (EhrUserSearchDetailDto userSearchDetailDto : userSet) {
            EhrUserDetail ehrUserDetail = new EhrUserDetail();
            ehrUserDetail.setName(userSearchDetailDto.getUserName());//中文名称
            ehrUserDetail.setAdCode(userSearchDetailDto.getUserCode());//邮箱前缀
            ehrUserDetail.setEmplid(userSearchDetailDto.getEmpCode());//员工工号
            ehrUserDetail.setUserType(userSearchDetailDto.getUserType());//类型 1 正式 2 外包
            ehrUserDetailList.add(ehrUserDetail);
        }
        long endTime = System.currentTimeMillis();
        log.info("调用ehr查询每个用的总时间长：" + (endTime - start));
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

    /***
     * 根据邮箱前缀获取用户的所有信息
     * @param userName
     */
    public OutsourcingPersonnelVo queryOutsourcingPersonnelFromMatrix(String userName) {
        JSONObject result = matrixApiClient.ajaxQueryOutsourcingPersonnel(userName);
        if (Objects.nonNull(result) && Objects.nonNull(result.getJSONObject("data"))) {
            return result.getJSONObject("data").toJavaObject(OutsourcingPersonnelVo.class);
        }
        return null;
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
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(emailPreList));
        Map<String, MatrixUserDetail> map = null;
        try {
            JSONObject result = matrixApiClient.getUserDetailByEmailPre(body);
            map = result.getObject("data", new TypeReference<Map<String, MatrixUserDetail>>() {
            });
        } catch (Exception e) {
            log.error("getUserDetailByEmailPre获取用户信息失败,emailPreList == {}：", emailPreList, e);
        }
        return map;
    }

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
