package com.spy.antoj.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求(自己)
 *
 * @TableName user
 */
@Data
public class UserUpdateMyRequest implements Serializable {
    /**
     * 用户昵称
     */
    private String userName;

    private static final long serialVersionUID = 1L;
}