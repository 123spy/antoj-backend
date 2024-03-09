package com.spy.antoj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spy.antoj.common.ErrorCode;
import com.spy.antoj.exception.BusinessException;
import com.spy.antoj.exception.ThrowUtils;
import com.spy.antoj.model.domain.Question;
import com.spy.antoj.model.domain.QuestionSubmit;
import com.spy.antoj.model.domain.User;
import com.spy.antoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.spy.antoj.model.enums.QuestionSubmitLanguageEnum;
import com.spy.antoj.model.enums.QuestionSubmitStatusEnum;
import com.spy.antoj.service.QuestionService;
import com.spy.antoj.service.QuestionSubmitService;
import com.spy.antoj.mapper.QuestionSubmitMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * @author spy
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2024-03-09 19:20:44
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 编程语言合法性
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        Long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        // 是否已经提交
        Long userId = loginUser.getId();
        // 每个用户串行提交
        QuestionSubmit questionSubmit = new QuestionSubmit();

        questionSubmit.setLanguage(language);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setJudgeInfo("{}");
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setQuestionId(questionSubmitAddRequest.getQuestionId());
        questionSubmit.setUserId(loginUser.getId());

        boolean save = this.save(questionSubmit);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR, "数据保存失败");
        Long questionSubmitId = questionSubmit.getId();

        // 异步判题
        CompletableFuture.runAsync(() -> {
            System.out.println("判题中");
        });

        return questionSubmitId;
    }
}




