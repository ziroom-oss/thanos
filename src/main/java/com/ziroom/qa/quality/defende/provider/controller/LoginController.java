package com.ziroom.qa.quality.defende.provider.controller;

import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.service.UserService;
import com.ziroom.qa.quality.defende.provider.vo.user.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "用户信息管理", tags = {"用户信息管理"})
@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @ApiOperation("注册用户")
    @PostMapping(value = "/userRegister")
    public RestResultVo userRegister(@RequestBody UserVo user) {
        userService.userRegister(user);
        return RestResultVo.fromSuccess("注册成功");
    }

    @ApiOperation("用户登陆")
    @PostMapping(value = "/userLogin")
    public RestResultVo userLogin(@RequestBody UserVo user) {
        return RestResultVo.fromData(userService.userLogin(user));
    }

    @ApiOperation("用户登出")
    @PostMapping(value = "/userLogout")
    public RestResultVo userLogout(@RequestHeader String userToken) {
        userService.userLogout(userToken);
        return RestResultVo.fromSuccess("登出成功");
    }

    @ApiOperation("用户注销")
    @PostMapping(value = "/userDelete")
    public RestResultVo userDelete(@RequestHeader String userToken) {
        userService.userDelete(userToken);
        return RestResultVo.fromSuccess("注销成功");
    }

    @ApiOperation("获取用户信息")
    @PostMapping(value = "/getByToken")
    public RestResultVo getByToken(@RequestHeader String userToken) {
        return RestResultVo.fromData(userService.getByToken(userToken));
    }

}
