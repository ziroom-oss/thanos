package com.ziroom.qa.quality.defende.provider.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.data.entity.AutoDataDatabase;
import com.ziroom.qa.quality.defende.provider.data.entity.dto.AutoDataDatabaseDto;
import com.ziroom.qa.quality.defende.provider.data.entity.dto.ExecSqlDto;
import com.ziroom.qa.quality.defende.provider.data.entity.dto.ExecSqlListDto;
import com.ziroom.qa.quality.defende.provider.data.entity.dto.GetTablesDto;
import com.ziroom.qa.quality.defende.provider.data.entity.vo.AutoDataDatabaseVo;
import com.ziroom.qa.quality.defende.provider.data.entity.vo.ExecSqlListVo;
import com.ziroom.qa.quality.defende.provider.data.entity.vo.ValidateConnectionVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mybatisPlusAutoGenerate
 * @since 2021-09-16
 */
public interface IAutoDataDatabaseService extends IService<AutoDataDatabase> {

    List<Map<String, Object>> execSelectSQL(ExecSqlDto execSqlDto);

    int execUpdateSQL(ExecSqlDto execSqlDto);

    List<String> getTables(GetTablesDto execSqlDto);

    ArrayList<AutoDataDatabaseVo> getAutoDataDatabaseVos(AutoDataDatabaseDto dto);

    List<AutoDataDatabase> getAutoDataDatabases(AutoDataDatabaseDto dto);

    ValidateConnectionVo validateConnection(AutoDataDatabaseDto dto);

    List<ExecSqlListVo> execSQLList(ExecSqlListDto dto);
}
