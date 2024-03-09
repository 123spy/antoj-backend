package com.spy.antoj.model.dto.user;

import com.spy.antoj.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询请求
 */
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户角色 user - 普通用户 admin - 管理员 ban - 禁号
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}
