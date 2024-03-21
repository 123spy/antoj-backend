package com.spy.antoj.model.dto.user;

import lombok.Data;

@Data
public class UserRecommendRequest {

    /**
     * 根据哪个post进行匹配
     */
    private Long userId;

    // 获取相似度最高的前N个
    private Long num;
}
