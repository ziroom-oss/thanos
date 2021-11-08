package com.ziroom.qa.quality.defende.provider.outinterface.client;

import com.github.lianjiatech.retrofit.spring.boot.annotation.RetrofitClient;
import com.ziroom.qa.quality.defende.provider.vo.wechat.Message;
import com.ziroom.qa.quality.defende.provider.vo.wechat.WechatResult;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

@RetrofitClient(baseUrl = "${wechat.url}")
public interface WeChatRobotApi {

    @Headers({ "Content-Type: application/json", "Accept: application/json" })
    @POST("/api/work/wechat/send")
    WechatResult sendWeChatMsg(@Body Message messageBody);
}
