package com.ziroom.qa.quality.defende.provider.outinterface.client.omega;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmegaApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private Long applicationId;
    private String applicationName;
    private String applicationDesc;
    private String gitUrl;
    private String language;
    private String teamCode;
    private String teamName;
    private String groupCode;
    private String groupName;
    private String projectName;
    private String namespace;
    private String appLevel;
}
