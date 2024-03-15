package com.spy.antoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spy.antoj.common.ErrorCode;
import com.spy.antoj.constant.CommonConstant;
import com.spy.antoj.exception.BusinessException;
import com.spy.antoj.exception.ThrowUtils;
import com.spy.antoj.judge.JudgeService;
import com.spy.antoj.model.domain.Question;
import com.spy.antoj.model.domain.QuestionSubmit;
import com.spy.antoj.model.domain.User;
import com.spy.antoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.spy.antoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.spy.antoj.model.enums.QuestionSubmitLanguageEnum;
import com.spy.antoj.model.enums.QuestionSubmitStatusEnum;
import com.spy.antoj.model.vo.QuestionSubmitVO;
import com.spy.antoj.model.vo.QuestionVO;
import com.spy.antoj.service.QuestionService;
import com.spy.antoj.service.QuestionSubmitService;
import com.spy.antoj.mapper.QuestionSubmitMapper;
import com.spy.antoj.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

    @Resource
    @Lazy
    private JudgeService judgeService;

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

        // 初始化信息 - 等待 0
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setJudgeInfo("{}");
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setQuestionId(questionSubmitAddRequest.getQuestionId());
        questionSubmit.setUserId(loginUser.getId());

        // 提交用户信息
        boolean save = this.save(questionSubmit);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR, "数据保存失败");
        Long questionSubmitId = questionSubmit.getId();

        // 异步判题
        CompletableFuture.runAsync(() -> {
            judgeService.doJudge(questionSubmitId);
        });

        return questionSubmitId;
    }

    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        int current = questionSubmitQueryRequest.getCurrent();
        int pageSize = questionSubmitQueryRequest.getPageSize();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();

        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit -> {
            QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
            return questionSubmitVO;
        }).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
}




