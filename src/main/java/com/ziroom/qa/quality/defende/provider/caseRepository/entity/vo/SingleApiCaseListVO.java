package com.ziroom.qa.quality.defende.provider.caseRepository.entity.vo;

import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApiCase;
import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleAssert;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/9/23 11:02 上午
 */
@Data
public class SingleApiCaseListVO extends AutoSingleApiCase {
    /**
     * omega 的应用名称
     */
    private String applicationName;
    /**
     * 接口分类名称
     */
    private String controllerName;

    private String requestUri;
    private String requestType;
    private String header;
    /**
     * follow
     */
    private boolean follow;

    private List<Long> listIn;

    private List<Long> listNotIn;

    private Integer runnable;

    private List<AutoSingleAssert> assertList;
    private String createUserName;
    private String updateUserName;

}
