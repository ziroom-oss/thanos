package com.ziroom.qa.quality.defende.provider.outinterface.client.omega;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmegaTeamInfo {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String teamCode;
    private String teamName;
    private String teamManager;
    private String teamManagerCode;
    private String teamQaUser;
    private String teamQaUserCode;
    private String teamManagerEmail;
    private String teamQaUserEmail;
}
