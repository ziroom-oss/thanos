package com.ziroom.qa.quality.defende.provider.outinterface.client;

import com.alibaba.fastjson.JSONObject;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 矩阵平台
 */
@RetrofitClient(baseUrl = "${matrix.baseurl}")
public interface MatrixApiClient {

    /**
     * 矩阵平台获取外包人员信息
     *
     * @param emailPre
     * @return
     */
    @GET("outsourcing-personnel/ajaxQueryOutsourcingPersonnel")
    JSONObject ajaxQueryOutsourcingPersonnel(@Query("emailPre") String emailPre);


    /**
     * 矩阵平台获取互联网中心的所有人员
     *
     * @param requestBody
     * @return
     */
    @POST("ehr/getUserDetailBySearchInfo")
    JSONObject getUserDetailBySearchInfoFromMatrix(@Body RequestBody requestBody);

    /**
     * 获取用户参数信息
     *
     * @param requestBody
     * @return
     */
    @POST("ehr/getUserDetailByEmailPre")
    JSONObject getUserDetailByEmailPre(@Body RequestBody requestBody);

}
