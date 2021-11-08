package com.ziroom.qa.quality.defende.provider.caseRepository.entity.dto;

import com.ziroom.qa.quality.defende.provider.caseRepository.entity.AutoSingleApiCase;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/9/23 10:47 上午
 */
@Data
public class SingleApiCaseListDto extends AutoSingleApiCase {
    /**
     * 请求路径
     */

    private String requestUri;
    /**
     * omega 的应用名称
     */
    private String applicationName;
    /**
     * 接口分类名称
     */
    private String controllerName;
    /**
     * follow
     */
    private boolean follow;

    private List<Long> listIn;

    private List<Long> listNotIn;

    private Integer runnable;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Long taskId;
}
