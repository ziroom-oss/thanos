package com.ziroom.qa.quality.defende.provider.data.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.data.entity.AutoDataDatabase;
import com.ziroom.qa.quality.defende.provider.data.entity.dto.AutoDataDatabaseDto;
import com.ziroom.qa.quality.defende.provider.data.entity.dto.ExecSqlDto;
import com.ziroom.qa.quality.defende.provider.data.entity.dto.ExecSqlListDto;
import com.ziroom.qa.quality.defende.provider.data.entity.dto.GetTablesDto;
import com.ziroom.qa.quality.defende.provider.data.entity.vo.AutoDataDatabaseVo;
import com.ziroom.qa.quality.defende.provider.data.entity.vo.ExecSqlListVo;
import com.ziroom.qa.quality.defende.provider.data.entity.vo.ValidateConnectionVo;
import com.ziroom.qa.quality.defende.provider.data.mapper.AutoDataDatabaseMapper;
import com.ziroom.qa.quality.defende.provider.data.service.IAutoDataDatabaseService;
import com.ziroom.qa.quality.defende.provider.outinterface.client.dto.ExecSqlOutDto;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-16
 */
@Service
public class AutoDataDatabaseServiceImpl extends ServiceImpl<AutoDataDatabaseMapper, AutoDataDatabase> implements IAutoDataDatabaseService {
    //    @Autowired
//    private TestBedFeignClient testBedFeignClient;
    @Override
    public List<Map<String, Object>> execSelectSQL(ExecSqlDto execSqlDto) {
        Integer databaseId = execSqlDto.getDatabaseId();
        AutoDataDatabase dataDatabase = this.getById(databaseId);
        ExecSqlOutDto dto = new ExecSqlOutDto();
        BeanUtils.copyProperties(dataDatabase, dto);
        dto.setSql(execSqlDto.getSql());
//        return testBedFeignClient.execSelectSql(dto).getData();
        return null;
    }

    @Override
    public int execUpdateSQL(ExecSqlDto execSqlDto) {
        Integer databaseId = execSqlDto.getDatabaseId();
        AutoDataDatabase dataDatabase = this.getById(databaseId);
        ExecSqlOutDto dto = new ExecSqlOutDto();
        BeanUtils.copyProperties(dataDatabase, dto);
        dto.setSql(execSqlDto.getSql());
//        return testBedFeignClient.execUpdateSql(dto).getData();
        return 0;
    }

    @Override
    public List<String> getTables(GetTablesDto execSqlDto) {
        Integer databaseId = execSqlDto.getDatabaseId();
        AutoDataDatabase dataDatabase = this.getById(databaseId);
        ExecSqlOutDto dto = new ExecSqlOutDto();
        BeanUtils.copyProperties(dataDatabase, dto);
//        return testBedFeignClient.getTables(dto).getData();
        return null;
    }

    @Override
    public ArrayList<AutoDataDatabaseVo> getAutoDataDatabaseVos(AutoDataDatabaseDto dto) {
        List<AutoDataDatabase> list = getAutoDataDatabases(dto);
        ArrayList<AutoDataDatabaseVo> resList = new ArrayList<>();
        for (AutoDataDatabase dataDatabase : list) {
            AutoDataDatabaseVo vo = new AutoDataDatabaseVo();
            BeanUtils.copyProperties(dataDatabase, vo);
            GetTablesDto getTablesDto = new GetTablesDto();
            getTablesDto.setDatabaseId(dataDatabase.getId());
            vo.setTablesList(this.getTables(getTablesDto));
            resList.add(vo);
        }
        return resList;
    }

    @Override
    public List<AutoDataDatabase> getAutoDataDatabases(AutoDataDatabaseDto dto) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("application_name", dto.getApplicationName());
        queryWrapper.eq("env", dto.getEnv());
        queryWrapper.eq("deleted", 0);
        List<AutoDataDatabase> list = this.list(queryWrapper);
        return list;
    }

    @Override
    public ValidateConnectionVo validateConnection(AutoDataDatabaseDto dto) {
        ExecSqlOutDto execDto = new ExecSqlOutDto();
        BeanUtils.copyProperties(dto, execDto);
//        return testBedFeignClient.validateConnection(execDto).getData();
        return null;
    }

    @Override
    public List<ExecSqlListVo> execSQLList(ExecSqlListDto dto) {
//      拆分sql 并判断类型
        List<String> sqlList = dto.getSqlList();
        List<ExecSqlListVo> list = new ArrayList<>();
        sqlList.stream().forEach(item -> {
            ExecSqlListVo execSqlListVo = new ExecSqlListVo();
            execSqlListVo.setSql(item);
            ExecSqlDto execSqlListDto = new ExecSqlDto();
            execSqlListDto.setSql(item);
            execSqlListDto.setDatabaseId(dto.getDatabaseId());
            if (item.toLowerCase(Locale.ROOT).contains("select")) {
                execSqlListVo.setSqlType("read");
                List<Map<String, Object>> maps = execSelectSQL(execSqlListDto);
                execSqlListVo.setData(JSON.toJSONString(maps, SerializerFeature.WriteNullStringAsEmpty));
            } else if (item.toLowerCase(Locale.ROOT).contains("insert") || item.toLowerCase(Locale.ROOT).contains("update") ||
                    item.toLowerCase(Locale.ROOT).contains("delete") || item.toLowerCase(Locale.ROOT).contains("truncate")) {
                execSqlListVo.setSqlType("write");
                Integer i = execUpdateSQL(execSqlListDto);
                execSqlListVo.setData(i);
            } else {
                throw new CustomException("非读写sql语句！");
            }
            list.add(execSqlListVo);
        });
        return list;
    }
}
