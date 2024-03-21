package com.spy.antoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spy.antoj.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spy.antoj.model.dto.user.UserQueryRequest;
import com.spy.antoj.model.vo.QuestionVO;
import com.spy.antoj.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author spy
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-03-02 15:10:26
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param user
     * @return
     */
    User getSafetyUser(User user);

    /**
     * 用户退出
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取用户视图
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 查询条件拼接
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 获取脱敏用户信息
     *
     * @param records
     * @return
     */
    List<UserVO> getUserVO(List<User> records);

    /**
     * 用户添加
     *
     * @param username
     * @param userAccount
     * @return
     */
    Long addUser(String username, String userAccount);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 获取登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    User getLoginUserPermitNull(HttpServletRequest request);
}
