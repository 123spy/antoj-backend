package com.spy.antoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spy.antoj.model.domain.Question;
import com.spy.antoj.model.domain.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spy.antoj.model.domain.User;
import com.spy.antoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.spy.antoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.spy.antoj.model.vo.QuestionSubmitVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author spy
 * @description 针对表【question_submit(题目提交)】的数据库操作Service
 * @createDate 2024-03-09 19:20:44
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 拼接查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 分页查询
     *
     * @param questionSubmitPage
     * @param request
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request);
}
