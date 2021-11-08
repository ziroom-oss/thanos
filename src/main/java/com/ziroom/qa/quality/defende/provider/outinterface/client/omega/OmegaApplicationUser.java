package com.ziroom.qa.quality.defende.provider.outinterface.client.omega;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmegaApplicationUser {
    private Long id;
    private Long applicationId;
    private String empName;
    private String empCode;
    private String adCode;
    private String userType;
    private Integer status;
}
