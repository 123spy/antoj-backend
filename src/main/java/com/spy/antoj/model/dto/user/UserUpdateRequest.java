package com.spy.antoj.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求
 *
 * @TableName user
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;


    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 电话
     */
    private String userProfile;

    /**
     * 用户角色 user - 普通用户 admin - 管理员 ban - 禁号
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}