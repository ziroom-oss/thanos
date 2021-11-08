package com.ziroom.qa.quality.defende.provider.outinterface.client;

import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.OmegaApplicationEnv;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ci")
public interface CiFeignClient {

    @GetMapping("api/omega/queryEnvByLevelAndDomain")
    RestResultVo<List<OmegaApplicationEnv>> queryEnvByLevelAndDomain(@RequestParam String envDomain, @RequestParam String envLevel);
}
