package com.ziroom.qa.quality.defende.provider.outinterface.client;

import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import com.ziroom.qa.quality.defende.provider.execTask.entity.dto.ArkAppDTO;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import retrofit2.http.POST;

import java.util.List;

/**
 * 方舟平台
 */
@RetrofitClient(baseUrl = "${ark.baseurl}")
public interface ArkApiClient {

    @POST("/api/app/getAllApp")
    RestResultVo<List<ArkAppDTO>> getAllApp();


}
