package com.ziroom.qa.quality.defende.provider.outinterface.client.omega;

import lombok.Data;

import java.util.List;

/**
 * Created by liangrk on 2021/7/26.
 */
@Data
public class ResourceVo {

    String appCode;

    List<Role> roleList;
    /**
     * 角色来源
     */
    String roleSource;
}
