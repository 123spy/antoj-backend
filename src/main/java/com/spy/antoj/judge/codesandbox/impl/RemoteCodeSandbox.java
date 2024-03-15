package com.spy.antoj.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.spy.antoj.common.ErrorCode;
import com.spy.antoj.exception.BusinessException;
import com.spy.antoj.judge.codesandbox.CodeSandbox;
import com.spy.antoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.spy.antoj.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程代码沙箱
 */
public class RemoteCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        String url = "http://192.168.198.133:8080/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .body(json)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "remoteSandbox error");
        }
//        System.out.println("远程代码沙箱请求成功 : " + JSONUtil.toBean(responseStr, ExecuteCodeResponse.class).toString());
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
