package com.ziroom.qa.quality.defende.provider.outinterface.client;

import com.alibaba.fastjson.JSONObject;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import retrofit2.http.GET;
import retrofit2.http.Query;

@RetrofitClient(baseUrl = "${ehr.baseurl}")
public interface EhrApiClient {

    @GET("api/ehr/getUserDetail.action")
    JSONObject getUserDetail(@Query("userCode") String userCode);

    @GET("api/ehr/getUsers.action")
    JSONObject getUsers(@Query("deptId") String deptId);

    @GET("api/ehr/getOrgByCode.action")
    JSONObject getOrgByCode(@Query("code") String code);

    @GET("api/ehr/getUserSimple.action")
    JSONObject getUserSimple(@Query("userEmail") String userEmail, @Query("page") Integer page, @Query("size") int size);

    @GET("/api/ehr/getChildOrgs.action")
    JSONObject getChildOrgs(@Query("parentId") String parentId, @Query("setId") String setId);


}
