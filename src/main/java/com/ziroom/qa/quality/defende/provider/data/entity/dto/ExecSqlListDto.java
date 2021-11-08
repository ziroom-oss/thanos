package com.ziroom.qa.quality.defende.provider.data.entity.dto;

import lombok.Data;

import java.util.List;


/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/9/16 4:58 下午
 */
@Data
public class ExecSqlListDto {
    private Integer databaseId;
    private List<String> sqlList;
}
