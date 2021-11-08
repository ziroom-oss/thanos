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
     * 注销
     *
     * @param userToken
     */
    void userDelete(String userToken);

    /**
     * 根据token获取用户信息
     *
     * @param userToken
     * @return
     */
    UserVo getByToken(String userToken);
}
