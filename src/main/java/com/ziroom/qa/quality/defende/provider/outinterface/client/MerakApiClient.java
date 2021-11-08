package com.ziroom.qa.quality.defende.provider.outinterface.client;

import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import com.ziroom.qa.quality.defende.provider.outinterface.client.omega.ResourceVo;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

@RetrofitClient(baseUrl = "${merak.url}")
public interface MerakApiClient {
    /**
     * 获取omega全量的项目信息，主要获取groupId和groupName
     * groupId为EHR的部门ID
     * @return omega全量项目信息
     */
    @GET("merak/listApp")
    RestResultVo<List<ResourceVo>> listApp(@Query("userCode") String userCode);


}
