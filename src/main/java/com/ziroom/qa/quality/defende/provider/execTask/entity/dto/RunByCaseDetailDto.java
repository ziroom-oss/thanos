package com.ziroom.qa.quality.defende.provider.execTask.entity.dto;

import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApiCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleAssert;
import lombok.Data;

import java.util.List;

/**
 * @author zhujj5
 * @Title: 运行用例详细内容
 * @Description:
 * @date 2021/9/27 10:37 上午
 */
@Data
public class RunByCaseDetailDto extends AutoSingleApiCase {
//  api
    private String applicationName;
    private String requestUri;
    private String requestType;
    private String header;
//  runDto
    private String env;
    private String protocolType;
    private String userCode;
//    asset
    List<AutoSingleAssert> assertList;
}
