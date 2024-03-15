package com.spy.antoj.judge.codesandbox.impl;

import com.spy.antoj.judge.codesandbox.CodeSandbox;
import com.spy.antoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.spy.antoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.spy.antoj.judge.codesandbox.model.JudgeInfo;
import com.spy.antoj.model.enums.JudgeInfoMessageEnum;
import com.spy.antoj.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * 示例代码沙箱
 */
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());

        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);

        return executeCodeResponse;
    }
}
