package com.ziroom.qa.quality.defende.provider.data.entity.vo;

import lombok.Data;

/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/9/28 2:53 下午
 */
@Data
public class ExecSqlListVo<T> {
    private String  sqlType;
    private String  sql;
    private T data;
}
