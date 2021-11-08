package com.ziroom.qa.quality.defende.provider.outinterface.client.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ziroom.qa.quality.defende.provider.outinterface.client.EhrApiClient;
import com.ziroom.qa.quality.defende.provider.result.RestResultVo;
import com.ziroom.qa.quality.defende.provider.vo.EhrDeptInfo;
import com.ziroom.qa.quality.defende.provider.vo.EhrDeptNewInfo;
import com.ziroom.qa.quality.defende.provider.vo.EhrDeptUserInfo;
import com.ziroom.qa.quality.defende.provider.vo.EhrUserDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class EhrService {

    @Autowired
    private EhrApiClient ehrApiClient;

    public RestResultVo<EhrUserDetail> getUserDetailFromEhr(String uid) {
        String param = "[" + uid + "]";
        JSONObject userDetailJSON = ehrApiClient.getUserDetail(param);
        String status = userDetailJSON.getString("status");
        if ("success".equals(status)) {
            JSONArray userDetailAarray = userDetailJSON.getJSONArray("data");
            List<EhrUserDetail> ehrUserDetailList = userDetailAarray.toJavaList(EhrUserDetail.class);
            if (ehrUserDetailList != null && ehrUserDetailList.size() > 0) {
                return RestResultVo.fromData(ehrUserDetailList.get(0));
            }
            return RestResultVo.fromErrorMessage(userDetailJSON.getString("message"));
        } else {
            return RestResultVo.fromErrorMessage(userDetailJSON.getString("message"));
        }
    }


    //http://ehrnew.ziroom.com/api/ehr/getUsers.action?deptId=101981
    public RestResultVo<List<EhrDeptUserInfo>> getUsersFromEhrBYDeptId(String deptId) {
        JSONObject users = ehrApiClient.getUsers(deptId);
        JSONArray data = users.getJSONArray("data");
        List<EhrDeptUserInfo> ehrDeptUserInfos = data.toJavaList(EhrDeptUserInfo.class);
        return RestResultVo.fromData(ehrDeptUserInfos);
    }

    //http://ehrnew.ziroom.com/api/ehr/getOrgByCode.action?code=101981
    public EhrDeptInfo getDeptInfoFromEhrByDeptId(String deptId) {
        JSONObject jsonObject = ehrApiClient.getOrgByCode(deptId);
        return jsonObject.getJSONObject("data").toJavaObject(EhrDeptInfo.class);
    }

    /**
     * @param userName
     * @return
     */
    //http://ehrnew.ziroom.com/api/ehr/getUserSimple.action?userEmail=zhangxl15&page=1&size=5
    public EhrUserDetail getEhrUserDetailByUsername(String userName) {
        EhrUserDetail ehrUserDetail = null;
        JSONArray ehrUserArray = ehrApiClient.getUserSimple(userName, 1, 1).getJSONArray("data");
        if (null != ehrUserArray && !ehrUserArray.isEmpty()) {
            ehrUserDetail = ehrUserArray.toJavaList(EhrUserDetail.class).get(0);
        }
        return ehrUserDetail;
    }

    public List<EhrDeptNewInfo> getChildEhrDeptList(String parentId) {
        JSONArray ehrDeptList = ehrApiClient.getChildOrgs(parentId, null).getJSONArray("data");
        if (Objects.nonNull(ehrDeptList)) {
            List<EhrDeptNewInfo> ehrDeptInfoList = ehrDeptList.toJavaList(EhrDeptNewInfo.class);
            return ehrDeptInfoList;
        }
        return null;
    }

    public String getUserNameByEmailPre(String emailPre) {
        if (StringUtils.isBlank(emailPre)) {
            return null;
        } else {
            EhrUserDetail detail = this.getEhrUserDetailByUsername(emailPre);
            if (Objects.isNull(detail)) {
                return null;
            }
            return detail.getName();
        }
    }
}
