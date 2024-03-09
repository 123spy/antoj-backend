package com.spy.antoj.service;

import com.spy.antoj.model.domain.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spy.antoj.model.domain.User;
import com.spy.antoj.model.dto.questionsubmit.QuestionSubmitAddRequest;

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
}
