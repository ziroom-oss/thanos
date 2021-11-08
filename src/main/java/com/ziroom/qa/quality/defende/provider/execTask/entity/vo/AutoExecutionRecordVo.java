package com.ziroom.qa.quality.defende.provider.execTask.entity.vo;

import com.ziroom.qa.quality.defende.provider.execTask.entity.AutoExecutionRecord;
import com.ziroom.qa.quality.defende.provider.execTask.entity.AutoExecutionRecordAssert;
import lombok.Data;

import java.util.List;

/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/9/26 6:06 下午
 */
@Data
public class AutoExecutionRecordVo extends AutoExecutionRecord {
    private List<AutoExecutionRecordAssert> list;
    private String caseName;
    private String applicationName;
}
