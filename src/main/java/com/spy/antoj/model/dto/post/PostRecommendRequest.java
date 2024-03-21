package com.spy.antoj.model.dto.post;

import com.spy.antoj.common.PageRequest;
import lombok.Data;

@Data
public class PostRecommendRequest {

    /**
     * 根据哪个post进行匹配
     */
    private Long postId;

    // 获取相似度最高的前N个
    private Long num;
}
