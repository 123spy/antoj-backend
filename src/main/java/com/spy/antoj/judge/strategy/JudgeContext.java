package com.spy.antoj.judge.strategy;

import com.spy.antoj.judge.codesandbox.model.JudgeInfo;
import com.spy.antoj.model.domain.Question;
import com.spy.antoj.model.domain.QuestionSubmit;
import com.spy.antoj.model.dto.question.JudgeCase;
import lombok.Data;

import java.util.List;

/**
 * 上下文
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;
}
