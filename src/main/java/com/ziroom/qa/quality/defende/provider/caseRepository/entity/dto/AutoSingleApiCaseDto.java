package com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto;

import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApiCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleAssert;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/9/23 2:19 下午
 */
@Data
public class AutoSingleApiCaseDto extends AutoSingleApiCase {
    /**
     * 请求路径
     */

    private String requestUri;
    @NotEmpty(message = "请求方式不能为空")
    private String requestType;
    /**
     * omega 的应用名称
     */
    private String applicationName;
    /**
     * 接口分类名称
     */
    private String controllerName;
    private String header;
    /**
     * 断言内容名
     */
    List<AutoSingleAssert> assertList;
    @NotEmpty(message = "用例名称不能为空")
    private String caseName;
}
