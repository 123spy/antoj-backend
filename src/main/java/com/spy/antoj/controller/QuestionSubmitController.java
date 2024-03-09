package com.spy.antoj.controller;

import com.spy.antoj.common.BaseResponse;
import com.spy.antoj.common.ErrorCode;
import com.spy.antoj.common.ResultUtils;
import com.spy.antoj.exception.BusinessException;
import com.spy.antoj.model.domain.User;
import com.spy.antoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.spy.antoj.service.QuestionSubmitService;
import com.spy.antoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {

    @Resource
    private UserService userService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @PostMapping("/")
    public BaseResponse<Long> doThumb(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 提交需要登录
        User loginUser = userService.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }
}
