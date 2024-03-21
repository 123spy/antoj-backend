package com.spy.antoj.judge;

import com.spy.antoj.judge.codesandbox.model.DebugCodeContent;
import com.spy.antoj.model.domain.QuestionSubmit;

public interface JudgeService {
    /**
     * 判题
     *
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);

    /**
     * Debug代码
     *
     * @param debugCodeContent
     */
    void doDebug(DebugCodeContent debugCodeContent);
}
