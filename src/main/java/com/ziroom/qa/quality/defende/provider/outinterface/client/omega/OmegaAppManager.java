package com.ziroom.qa.quality.defende.provider.outinterface.client.omega;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmegaAppManager {
    private Integer appId;
    private String appCode;
    private List<String> appManagerAd;
}
