package com.ziroom.qa.quality.defende.provider.outinterface.client;

import com.alibaba.fastjson.JSONObject;
import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * jira平台
 */
@RetrofitClient(baseUrl = "${jira.baseurl}")
public interface JiraApiClient {

    /**
     * 获取jira所属部门
     *
     * @return
     */
    @GET("/rest/api/2/groups/picker")
    JSONObject getJiraGroup(@Query("maxResults") Integer maxResults, @Query("query") String query);


}
