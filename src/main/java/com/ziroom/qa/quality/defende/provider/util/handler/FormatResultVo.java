package com.ziroom.qa.quality.defende.provider.util.handler;

import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleAssert;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhujj5
 * @Title: 返回数值格式化
 * @Description:
 * @date 2021/9/26 5:29 下午
 */
@Data
public class FormatResultVo {
    private String actualResult;
    private String responseHeader;
    private String responseTime;
    private String requestUrl;
    private String requestType;
    private String requestBody;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
//    断言类型和内容
    private List<AutoSingleAssert> list;

}
