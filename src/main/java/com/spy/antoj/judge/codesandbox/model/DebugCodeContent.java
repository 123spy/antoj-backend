package com.spy.antoj.judge.codesandbox.model;

import com.spy.antoj.model.dto.question.JudgeCase;
import lombok.Data;

@Data
public class DebugCodeContent {
    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 调试代码
     */
    private String code;

    /**
     * 代码语言
     */
    private String language;

    /**
     * 调试代码用例
     */
    private String inputCase;

    /**
     * 用户id
     */
    private Long userId;
}
