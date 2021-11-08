package com.ziroom.qa.quality.defende.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ziroom.qa.quality.defende.provider.entity.User;
import com.ziroom.qa.quality.defende.provider.mapper.UserMapper;
import com.ziroom.qa.quality.defende.provider.result.CustomException;
import com.ziroom.qa.quality.defende.provider.service.UserService;
import com.ziroom.qa.quality.defende.provider.util.MD5Util;
import com.ziroom.qa.quality.defende.provider.util.RedisUtil;
import com.ziroom.qa.quality.defende.provider.util.idgen.IdGenUtil;
import com.ziroom.qa.quality.defende.provider.vo.user.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public User getUserInfoByUid(String uid) {
        if (StringUtils.isBlank(uid) || StringUtils.isBlank(uid.trim())) {
            return null;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 根据用户userName获取用户信息
     *
     * @param userName
     * @return
     */
    @Override
    public User getUserInfoByUserName(String userName) {
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(userName.trim())) {
            return null;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName);
        queryWrapper.orderByDesc("id");
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 根据用户英文名称获取用户信息集合
     *
     * @param userName
     * @return
     */
    @Override
    public List<User> getUserInfoLikeUserName(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("user_name", userName);
        return userMapper.selectList(queryWrapper);
    }

    /**
     * 注册
     *
     * @param user
     * @return
     */
    @Override
    public UserVo userRegister(UserVo user) {
        User u = this.getUserInfoByUserName(user.getUserName());
        if (Objects.nonNull(u)) {
            throw new CustomException("用户已存在!!!");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            throw new CustomException("密码不能为空!!!");
        }
        User newUser = new User();
        newUser.setUserName(user.getUserName());
        newUser.setUserType(2);
        newUser.setRole("user");
        newUser.setTreePath(user.getTreePath());
        newUser.setEhrGroup("");
        newUser.setPassword(MD5Util.MD5Encode(user.getPassword()));
        newUser.setUid(IdGenUtil.nextId() + "");
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());
        this.save(newUser);
        return null;
    }

    /**
     * 登陆
     *
     * @param user
     * @return
     */
    @Override
    public UserVo userLogin(UserVo user) {
        if (StringUtils.isBlank(user.getPassword()) ||
                StringUtils.isBlank(user.getUserName())) {
            throw new CustomException("用户/密码不能为空！！！");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", user.getUserName());
        queryWrapper.eq("password", MD5Util.MD5Encode(user.getPassword()));
        List<User> userList = super.list(queryWrapper);
        if (CollectionUtils.isEmpty(userList)) {
            throw new CustomException("用户/密码不正确！！！");
        }

        String oldToken = (String) redisUtil.get(user.getUserName());
        redisUtil.deleteByKey(oldToken);

        String token = UUID.randomUUID().toString().replace("-", "");
        log.info("login userName === {},token === {}", user.getUserName(), token);
        BeanUtils.copyProperties(userList.get(0), user);
        user.setPassword("");
        user.setUserToken(token);

        redisUtil.setOneDay(token, user);
        redisUtil.setOneDay(user.getUserName(), token);

        return user;
    }

    /**
     * 登出
     *
     * @param userToken
     */
    @Override
    public void userLogout(String userToken) {
        UserVo userVo = this.getByToken(userToken);
        if (Objects.isNull(userVo)) {
            throw new CustomException("该用户登陆信息不存在！！！");
        }
        redisUtil.deleteByKey(userVo.getUserName());
        redisUtil.deleteByKey(userToken);
    }

    /**
     * 注销
     *
     * @param userToken
     */
    @Override
    public void userDelete(String userToken) {
        UserVo userVo = this.getByToken(userToken);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userVo.getUserName());
        boolean flag = super.remove(queryWrapper);
        if (flag) {
            this.userLogout(userToken);
        }
    }

    @Override
    public UserVo getByToken(String userToken) {
        if (StringUtils.isBlank(userToken)) {
            return null;
        }
        return (UserVo) redisUtil.get(userToken);
    }

}
