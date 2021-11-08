package com.ziroom.qa.quality.defende.provider.outinterface.client.omega;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 *@description:用户角色表
 *@author:liangrk
 *@date:2021-07-21
 *@since:1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名
     */
    private String roleName;

    /**
     * 类型，备用字段
     */
    private String type;

    /**
     * 来源，备用字段
     */
    private String source;

    /**
     * 归属系统
     */
    private String system;

    /**
     * 说明
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否删除 0否 1是
     */
    private Integer isDel;


}
