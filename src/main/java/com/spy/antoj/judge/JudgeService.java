package com.spy.antoj.judge;

import com.spy.antoj.model.domain.QuestionSubmit;

public interface JudgeService {
    /**
     * 判题
     *
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);
}
