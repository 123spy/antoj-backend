package com.spy.antoj.judge.strategy;

import com.spy.antoj.judge.codesandbox.model.JudgeInfo;

public interface JudgeStrategy {

    JudgeInfo doJudge(JudgeContext judgeContext);
}
