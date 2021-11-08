package com.ziroom.qa.quality.defende.provider.controller;

import com.alibaba.fastjson.JSON;
import com.ziroom.qa.quality.defende.provider.constant.enums.UserRoleEnum;
import com.ziroom.qa.quality.defende.provider.entity.User;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.MatrixService;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.service.UserService;
import com.ziroom.qa.quality.defende.provider.vo.MatrixUserDetail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Api(value = "用户信息管理", tags = {"用户信息管理"})
@Slf4j
@RestController
@RequestMapping("/userInfo")
public class UserInfoController {

    @Autowired
    private UserService userService;
    @Autowired
    private MatrixService matrixService;

    @ApiOperation("获取用户角色信息")
    @GetMapping(value = "/queryUserRoleByUid")
    public RestResultVo<String> queryUserRoleByUid(@RequestHeader String uid) {
        RestResultVo<String> restResultVo;
        try {
            User user = userService.getUserInfoByUid(uid);
            if (user != null) {
                restResultVo = RestResultVo.fromData(user.getRole());
            } else {
                restResultVo = RestResultVo.fromErrorMessage("没有查询到用户信息");
            }
        } catch (Exception e) {
            log.error("/userInfo/queryUserRoleByUid 获取用户角色信息失败，uid=={}", uid, e);
            restResultVo = RestResultVo.fromErrorMessage("失败");
        }
        return restResultVo;
    }

    @ApiOperation("获取用户角色信息byusername")
    @GetMapping(value = "/queryUserRoleByUserName")
    public RestResultVo<String> queryUserRoleByUserName(@RequestHeader String userName) {
        RestResultVo<String> restResultVo;
        try {
            User user = userService.getUserInfoByUserName(userName);
            if (user != null) {
                restResultVo = RestResultVo.fromData(user.getRole());
            } else {
                restResultVo = RestResultVo.fromErrorMessage("没有查询到用户信息");
            }
        } catch (Exception e) {
            log.error("/userInfo/queryUserRoleByUserName 获取用户角色信息byusername失败，userName=={}", userName, e);
            restResultVo = RestResultVo.fromErrorMessage(e.getMessage());
        }
        return restResultVo;
    }

    @ApiOperation("更新用户权限信息")
    @PostMapping("/createOrupdateUserRole")
    public RestResultVo createOrupdateUserRole(@RequestHeader String userName,
                                               @RequestHeader String uid,
                                               @RequestHeader String userType,
                                               @RequestParam String userRole) {
        if (!UserRoleEnum.getUserRoleList().contains(userRole)) {
            return RestResultVo.fromErrorMessage("需要更改的角色信息不正确");
        }
        User user = userService.getUserInfoByUserName(userName);
        if (Objects.isNull(user)) {
            user = new User();
            user.setUid(uid);
            user.setCreateTime(LocalDateTime.now());
        }
        // 获取登陆人员信息
        Map<String, MatrixUserDetail> userMap = matrixService.getUserDetailByEmailPre(Arrays.asList(userName));
        MatrixUserDetail userDetail = userMap.get(userName);
        if (Objects.isNull(userDetail)) {
            return RestResultVo.fromErrorMessage("登陆用户信息不正确！");
        }
        log.info("拦截器用户信息为：userName==={}，userDetail === {}", userName, JSON.toJSONString(userDetail));
        //3. 保存或更新用户
        user.setRole(userRole);
        user.setNickName(userDetail.getUserName());
        user.setTreePath(userDetail.getTreePath().substring(7).replace("-", ","));
        user.setEhrGroup(userDetail.getDeptName());

        user.setUserName(userName);
        user.setUpdateTime(LocalDateTime.now());
        user.setUserType(Integer.parseInt(userType));
        user.setUpdateTime(LocalDateTime.now());
        userService.saveOrUpdate(user);
        return RestResultVo.fromSuccess("成功");
    }

}
