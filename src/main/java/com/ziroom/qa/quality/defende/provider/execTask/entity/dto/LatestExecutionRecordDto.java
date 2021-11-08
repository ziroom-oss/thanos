package com.ziroom.qa.quality.defende.provider.execTask.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/10/8 11:43 上午
 */
@Data
public class LatestExecutionRecordDto {
//    最新条数 不传给10
    private Integer lastSize=10;
//    用例ID列表
    private List<Integer> caseIdList;
}
