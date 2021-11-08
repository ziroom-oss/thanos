package com.ziroom.qa.quality.defende.provider.outinterface.client.omega;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmegaApplicationCi implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer projectId;

    private Integer applicationId;

    private String environmentLevel;

    private String ruleType;

    private Boolean enabled;

}
