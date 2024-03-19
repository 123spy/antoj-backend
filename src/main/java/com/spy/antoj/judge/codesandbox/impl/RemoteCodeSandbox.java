package com.spy.antoj.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.spy.antoj.common.ErrorCode;
import com.spy.antoj.exception.BusinessException;
import com.spy.antoj.judge.codesandbox.CodeSandbox;
import com.spy.antoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.spy.antoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.spy.antoj.judge.codesandbox.model.JudgeInfo;
import com.spy.antoj.model.enums.JudgeInfoMessageEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * 远程代码沙箱
 */
public class RemoteCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        String url = "http://120.27.111.16:8080/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        ExecuteCodeResponse executeCodeResponse = null;
        try {
            // 发送请求
            String responseStr = HttpUtil.createPost(url)
                    .body(json)
                    .execute()
                    .body();

            // 执行正常
            if (StringUtils.isBlank(responseStr)) {
                throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "remoteSandbox error");
            }

            executeCodeResponse = JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
        } catch (Exception e) {
            // 执行错误时
            executeCodeResponse = new ExecuteCodeResponse();
            executeCodeResponse.setStatus(3);
            executeCodeResponse.setMessage("");
            executeCodeResponse.setOutputList(new ArrayList<>());
            JudgeInfo judgeInfo = new JudgeInfo();
            judgeInfo.setTime(0L);
            judgeInfo.setMemory(0L);
            judgeInfo.setMessage(JudgeInfoMessageEnum.REMOTE_ERROR.getText());
            executeCodeResponse.setJudgeInfo(judgeInfo);
        }

        System.out.println("远程代码沙箱请求结束 : " + executeCodeResponse.toString());
        return executeCodeResponse;
    }
}
