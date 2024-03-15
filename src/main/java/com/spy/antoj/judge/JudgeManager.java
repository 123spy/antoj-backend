package com.spy.antoj.judge;

import com.spy.antoj.judge.codesandbox.model.JudgeInfo;
import com.spy.antoj.judge.strategy.DefaultJudgeStrategy;
import com.spy.antoj.judge.strategy.JavaLanguageJudgeStrategy;
import com.spy.antoj.judge.strategy.JudgeContext;
import com.spy.antoj.judge.strategy.JudgeStrategy;
import com.spy.antoj.model.domain.QuestionSubmit;
import org.springframework.stereotype.Service;

@Service
public class JudgeManager {

    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
