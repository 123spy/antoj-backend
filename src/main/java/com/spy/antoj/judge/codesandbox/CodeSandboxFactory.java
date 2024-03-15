package com.spy.antoj.judge.codesandbox;

import com.spy.antoj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.spy.antoj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.spy.antoj.judge.codesandbox.impl.ThirdPartyCodeSandbox;

/**
 * 代码沙箱工厂
 */
public class CodeSandboxFactory {

    public static CodeSandbox newInstance(String type) {
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
