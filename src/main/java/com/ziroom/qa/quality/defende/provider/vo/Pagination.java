package com.ziroom.qa.quality.defende.provider.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

@Data
public class Pagination<T> {

    /** 分页对象 */
    private Page<T> page;

    /** 查询对象 */
    private T searchObj;

}
