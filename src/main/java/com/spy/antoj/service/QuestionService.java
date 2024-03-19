package com.spy.antoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spy.antoj.model.domain.Question;
import com.spy.antoj.model.domain.Question;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spy.antoj.model.dto.question.QuestionQueryRequest;
import com.spy.antoj.model.vo.QuestionAdminVO;
import com.spy.antoj.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author spy
 * @description 针对表【question(题目)】的数据库操作Service
 * @createDate 2024-03-09 19:20:39
 */
public interface QuestionService extends IService<Question> {
    /**
     * 参数校验
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取Question视图
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * 获取题目（管理员视图）
     *
     * @param question
     * @param request
     * @return
     */
    QuestionAdminVO getQuestionAdminVO(Question question, HttpServletRequest request);
}
