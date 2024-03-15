package com.spy.antoj.model.dto.questionsubmit;

import com.spy.antoj.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
