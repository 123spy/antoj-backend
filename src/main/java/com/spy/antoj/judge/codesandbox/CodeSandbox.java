package com.spy.antoj.judge.codesandbox;

import com.spy.antoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.spy.antoj.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 沙箱定义接口
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
