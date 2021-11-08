package com.ziroom.qa.quality.defende.provider.outinterface.client;

import com.alibaba.fastjson.JSONObject;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import com.ziroom.qa.quality.defende.provider.outinterface.client.omega.DetailApplicationVo;
import com.ziroom.qa.quality.defende.provider.outinterface.client.omega.OmegaPageInfo;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * omega平台
 */
@RetrofitClient(baseUrl = "${omega.baseurl}")
public interface OmegaApiClient {

    @POST("/api/out/allApp")
    JSONObject allAppByApplicationNameLK(@Query("applicationNameLK") String applicationNameLK);

    @POST("/api/out/allApp")
    RestResultVo<OmegaPageInfo<DetailApplicationVo>> allAppByProjectName(@Query("applicationNameLK") String applicationName);

    @POST("/api/out/allApp")
    RestResultVo<OmegaPageInfo<DetailApplicationVo>> allAppByDomainNameLK(@Query("domainNameLK") String domainNameLK);


}
