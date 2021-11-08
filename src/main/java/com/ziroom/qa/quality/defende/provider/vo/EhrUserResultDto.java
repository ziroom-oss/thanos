package com.ziroom.qa.quality.defende.provider.vo;

import lombok.Data;

import java.util.List;

@Data
public class EhrUserResultDto {
    /**
     * 正式员工数量
     */
    private int officialUserCount;
    /**
     * 外包人员数量
     */
    private int compactUserCount;
    /**
     * 实习生数量
     */
    private int practiceUserCount;

    /**
     * 员工详情
     */
   private List<EhrUserSearchDetailDto> userList;
}
