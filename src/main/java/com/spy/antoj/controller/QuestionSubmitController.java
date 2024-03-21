package com.spy.antoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spy.antoj.common.BaseResponse;
import com.spy.antoj.common.ErrorCode;
import com.spy.antoj.common.ResultUtils;
import com.spy.antoj.exception.BusinessException;
import com.spy.antoj.exception.ThrowUtils;
import com.spy.antoj.model.domain.QuestionSubmit;
import com.spy.antoj.model.domain.User;
import com.spy.antoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.spy.antoj.model.dto.QuestionDebug.QuestionDebugRequest;
import com.spy.antoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.spy.antoj.model.vo.QuestionSubmitVO;
import com.spy.antoj.service.QuestionSubmitService;
import com.spy.antoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/question_submit")
@Slf4j
@CrossOrigin
public class QuestionSubmitController {

    @Resource
    private UserService userService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @PostMapping("/")
    public BaseResponse<Long> doSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 提交需要登录
        User loginUser = userService.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    @PostMapping("/debug")
    public BaseResponse<Long> doDebug(@RequestBody QuestionDebugRequest questionDebugRequest, HttpServletRequest request) {
        if (questionDebugRequest == null || questionDebugRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 提交需要登录
        User loginUser = userService.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionDebug(questionDebugRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    @GetMapping("/get/vo")
    public BaseResponse<QuestionSubmitVO> getQuestionSubmitVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionSubmit questionSubmit = questionSubmitService.getById(id);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (!questionSubmit.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        return ResultUtils.success(questionSubmitVO);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        if (questionSubmitQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int current = questionSubmitQueryRequest.getCurrent();
        int pageSize = questionSubmitQueryRequest.getPageSize();
        // todo 爬虫限制
        ThrowUtils.throwIf(pageSize > 50, ErrorCode.PARAMS_ERROR);
        Page<QuestionSubmit> questionPage = questionSubmitService.page(new Page<>(current, pageSize),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        Page<QuestionSubmitVO> questionVOPage = questionSubmitService.getQuestionSubmitVOPage(questionPage, request);
        return ResultUtils.success(questionVOPage);
    }
}
