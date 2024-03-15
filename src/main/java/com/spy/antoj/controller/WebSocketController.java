package com.spy.antoj.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.spy.antoj.common.BaseResponse;
import com.spy.antoj.common.ResultUtils;
import com.spy.antoj.common.WebSocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/ws")
@Slf4j
public class WebSocketController {

    @Resource
    private WebSocketUtils webSocketUtils;

    @PostMapping("/")
    public BaseResponse<String> getWs(long id, HttpServletRequest request) {
        //创建业务消息信息
        JSONObject obj = new JSONObject();
        obj.put("信息", "测试获取Socket");//业务类型
        //全体发送
//        webSocket.sendAllMessage(JSONUtil.toJsonStr(obj));
        webSocketUtils.sendOneMessage(String.valueOf(id), JSONUtil.toJsonStr(obj));
        return ResultUtils.success("Hello, world");
    }
}
