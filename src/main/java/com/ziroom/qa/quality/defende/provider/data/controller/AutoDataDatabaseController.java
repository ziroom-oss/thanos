package com.ziroom.qa.quality.defende.provider.data.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ziroom.qa.quality.defende.provider.data.entity.dto.AutoDataDatabaseDto;
import com.ziroom.qa.quality.defende.provider.data.entity.dto.ExecSqlDto;
import com.ziroom.qa.quality.defende.provider.data.entity.dto.ExecSqlListDto;
import com.ziroom.qa.quality.defende.provider.data.entity.vo.AutoDataDatabaseVo;
import com.ziroom.qa.quality.defende.provider.data.service.IAutoDataDatabaseService;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-16
 */
@RestController
@RequestMapping("/data/auto-data-database")
@Api("数据库管理")
@Valid
public class AutoDataDatabaseController {

    @Autowired
    private IAutoDataDatabaseService autoDataDatabaseService;
    @RequestMapping("save")
    @ApiOperation("添加数据库")
    public RestResultVo save(@Validated @RequestBody AutoDataDatabaseDto autoDataDatabaseDto){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("application_name",autoDataDatabaseDto.getApplicationName());
        queryWrapper.eq("env",autoDataDatabaseDto.getEnv());
        queryWrapper.eq("deleted",0);
        int count = autoDataDatabaseService.count(queryWrapper);
        if(count>0){
            throw new CustomException("每个环境最多绑定一个数据库!");
        }
        autoDataDatabaseService.save(autoDataDatabaseDto);
        return RestResultVo.fromSuccess("success!");
    }
    @RequestMapping("removeById")
    @ApiOperation("删除数据库")
    public RestResultVo removeById(@RequestParam Long id){
        autoDataDatabaseService.removeById(id);
        return RestResultVo.fromSuccess("success!");
    }
    @RequestMapping("updateById")
    @ApiOperation("修改数据库")
    public RestResultVo updateById(@RequestBody AutoDataDatabaseDto autoDataDatabaseDto){
        autoDataDatabaseService.updateById(autoDataDatabaseDto);
        return RestResultVo.fromSuccess("success!");
    }
    @RequestMapping("list")
    @ApiOperation("查询数据库")
    public RestResultVo list(@RequestBody AutoDataDatabaseDto dto){
        ArrayList<AutoDataDatabaseVo> resList = autoDataDatabaseService.getAutoDataDatabaseVos(dto);
        return RestResultVo.fromData(resList);
    }

    @RequestMapping("execSelectSQL")
    @ApiOperation("execSelectSQL")
    public RestResultVo execSelectSQL(@RequestBody ExecSqlDto dto){
        return RestResultVo.fromData(autoDataDatabaseService.execSelectSQL(dto));
    }
    @RequestMapping("execUpdateSQL")
    @ApiOperation("执行sql")
    public RestResultVo execUpdateSQL(@RequestBody ExecSqlDto dto){
        return RestResultVo.fromData(autoDataDatabaseService.execUpdateSQL(dto));
    }
    @RequestMapping("execSQLList")
    @ApiOperation("执行sql")
    public RestResultVo execSQLList(@RequestBody ExecSqlListDto dto){
        return RestResultVo.fromData(autoDataDatabaseService.execSQLList(dto));
    }
    @RequestMapping("validateConnection")
    @ApiOperation("执行sql")
    public RestResultVo validateConnection(@RequestBody AutoDataDatabaseDto dto){
        return RestResultVo.fromData(autoDataDatabaseService.validateConnection(dto));
    }
}