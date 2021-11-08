package com.ziroom.qa.quality.defende.provider.controller;

import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.outinterface.client.service.MatrixService;
import com.ziroom.qa.quality.defende.provider.vo.EhrUserDetail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(value = "矩阵平台外部接口", tags = {"矩阵平台外部接口"})
@RestController
@RequestMapping("/matrix")
public class MatrixController {

    @Autowired
    private MatrixService matrixService;

    @ApiOperation("互联网平台的用户详情列表保存到Redis")
    @GetMapping("/saveInternetPlatEhrUserListToRedis")
    public RestResultVo saveInternetPlatEhrUserListToRedis(@RequestParam(name = "deptCode", required = false) String deptCode) {
        if (StringUtils.isBlank(deptCode)) {
            deptCode = "101877";
        }
        try {
            matrixService.saveInternetPlatEhrUserListToRedis(deptCode);
            return RestResultVo.fromData("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return RestResultVo.fromErrorMessage("更新失败");
        }
    }

    @ApiOperation("根据部门ID获取互联网用户列表")
    @GetMapping("/getInternetPlatEhrUserListFromMatrix")
    public RestResultVo<List<EhrUserDetail>> getInternetPlatEhrUserListFromMatrix(@RequestParam(name = "deptCode", required = false) String deptCode) {
        List<EhrUserDetail> ehrUserDetailList = matrixService.getMatrixUserRedisInfo(deptCode);
        return RestResultVo.fromData(ehrUserDetailList);
    }

    @ApiOperation("根据用户名称查询用户列表")
    @GetMapping("/getEhrUserDetailLikeUserName")
    public RestResultVo<List<EhrUserDetail>> getEhrUserDetailLikeUserName(
            @RequestParam(name = "userName") String userName) {
        List<EhrUserDetail> ehrUserDetailList = matrixService.getEhrUserDetailLikeUserName(userName);
        return RestResultVo.fromData(ehrUserDetailList);
    }
}
