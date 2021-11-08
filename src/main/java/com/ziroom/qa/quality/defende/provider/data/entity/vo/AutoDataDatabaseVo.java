package com.ziroom.qa.quality.defende.provider.data.entity.vo;

import com.ziroom.qa.quality.defende.provider.data.entity.AutoDataDatabase;
import lombok.Data;

import java.util.List;

/**
 * @author zhujj5
 * @Title:
 * @Description:
 * @date 2021/9/24 3:56 下午
 */
@Data
public class AutoDataDatabaseVo extends AutoDataDatabase {
    private List<String> tablesList;
}
