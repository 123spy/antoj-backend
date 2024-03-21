package com.spy.antoj.model.dto.QuestionDebug;

import com.spy.antoj.model.dto.question.JudgeCase;
import lombok.Data;

import java.io.Serializable;

/**
 * 题目提交
 */
@Data
public class QuestionDebugRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 输入样例
     */
    private String input;

    private static final long serialVersionUID = 1L;
}