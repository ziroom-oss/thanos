package com.ziroom.qa.quality.defende.provider.execTask.entity.dto;

import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApiCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleAssert;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author zhujj5
 * @Title: SUT请求实体
 * @Description:
 * @date 2021/9/26 2:15 下午
 */
@Data
public class SUTRequestDto extends AutoSingleApiCase {

    private String requestUri;
    private String header;
    private String requestType;
    /**
     * 断言内容名
     */
    @NotEmpty
    List<AutoSingleAssert> assertList;
}
