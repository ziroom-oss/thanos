package com.ziroom.qa.quality.defende.provider.execTask.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/9/24 5:53 下午
 */
@Data
public class RunCaseByIdListDto {
    private List<Integer> caseIdList;
    private String env;
    private String userCode;
}
