package com.ziroom.qa.quality.defende.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ziroom.qa.quality.defende.provider.entity.User;
import com.ziroom.qa.quality.defende.provider.vo.user.UserVo;

import java.util.List;

public interface UserService extends IService<User> {

    /**
     * 根据用户id获取用户信息
     *
     * @param uid
     * @return
     */
    User getUserInfoByUid(String uid);

    /**
     * 根据用户id获取用户信息
     *
     * @param userName
     * @return
     */
    User getUserInfoByUserName(String userName);

    /**
     * 根据用户英文名称获取用户信息集合
     *
     * @param userName
     * @return
     */
    List<User> getUserInfoLikeUserName(String userName);

    /**
     * 注册
     *
     * @param user
     * @return
     */
    UserVo userRegister(UserVo user);

    /**
     * 登陆
     *
     * @param user
     * @return
     */
    UserVo userLogin(UserVo user);

    /**
     * 登出
     *
     * @param userToken
     */
    void userLogout(String userToken);

    /**
     * 删除
     *
     * @param userToken
     * @param userName
     */
    void userDelete(String userToken, String userName);

    /**
     * 根据token获取用户信息
     *
     * @param userToken
     * @return
     */
    UserVo getByToken(String userToken);

    /**
     * 根据用户英文名称集合获取用户信息集合
     *
     * @param userNameList
     * @return
     */
    List<User> getUserListByUserNames(List<String> userNameList);

    /**
     * 根据部门编号获取用户信息
     *
     * @param deptCode
     * @return
     */
    List<User> getUserListByDeptCode(String deptCode);

    /**
     * 修改密码
     * @param user
     */
    void userUpdatePwd(UserVo user);
}
