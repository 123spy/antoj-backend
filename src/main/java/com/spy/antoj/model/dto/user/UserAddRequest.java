package com.spy.antoj.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户添加请求
 * @TableName user
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    private static final long serialVersionUID = 1L;
}