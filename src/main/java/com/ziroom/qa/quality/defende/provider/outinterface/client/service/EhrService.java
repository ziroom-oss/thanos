package com.ziroom.qa.quality.defende.provider.outinterface.client.service;

import com.ziroom.qa.quality.defende.provider.entity.User;
import com.ziroom.qa.quality.defende.provider.service.UserService;
import com.ziroom.qa.quality.defende.provider.vo.EhrDeptInfo;
import com.ziroom.qa.quality.defende.provider.vo.EhrDeptNewInfo;
import com.ziroom.qa.quality.defende.provider.vo.EhrUserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 可以接入公司内容用户管理信息
 */
@Slf4j
@Service
public class EhrService {

    @Autowired
    private UserService userService;

    /**
     * 根据用户名获取用户详细信息
     *
     * @param userName
     * @return
     */
    public EhrUserDetail getEhrUserDetailByUsername(String userName) {
        EhrUserDetail ehrUserDetail = new EhrUserDetail();
        User user = userService.getUserInfoByUserName(userName);
        if (Objects.nonNull(user)) {
            ehrUserDetail.setName(user.getNickName());
        }
        return ehrUserDetail;
    }

    /**
     * 根据父部门id获取子部门信息
     *
     * @param parentId
     * @return
     */
    public List<EhrDeptNewInfo> getChildEhrDeptList(String parentId) {
//        JSONArray ehrDeptList = ehrApiClient.getChildOrgs(parentId, null).getJSONArray("data");
//        if (Objects.nonNull(ehrDeptList)) {
//            List<EhrDeptNewInfo> ehrDeptInfoList = ehrDeptList.toJavaList(EhrDeptNewInfo.class);
//            return ehrDeptInfoList;
//        }
        return null;
    }

    public EhrDeptInfo getDeptInfoFromEhrByDeptId(String deptId) {
        EhrDeptInfo info = new EhrDeptInfo();
        info.setDescrShort("测试部门");
        return info;
    }
}
