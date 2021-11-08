package com.ziroom.qa.quality.defende.provider.outinterface.client.omega;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *
 * </p>
 *
 * @author jobob
 * @since 2020-12-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmegaOpenCr {

    /**
     * 项目ID作为主键
     */
    @TableId(type = IdType.INPUT)
    private Integer projectId;

    private Integer isOpen;
}
