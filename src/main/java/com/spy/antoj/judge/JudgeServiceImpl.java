package com.spy.antoj.judge;

import cn.hutool.json.JSONUtil;
import com.spy.antoj.common.ErrorCode;
import com.spy.antoj.common.WebSocketUtils;
import com.spy.antoj.exception.BusinessException;
import com.spy.antoj.exception.ThrowUtils;
import com.spy.antoj.judge.codesandbox.CodeSandbox;
import com.spy.antoj.judge.codesandbox.CodeSandboxFactory;
import com.spy.antoj.judge.codesandbox.CodeSandboxProxy;
import com.spy.antoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.spy.antoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.spy.antoj.judge.codesandbox.model.JudgeInfo;
import com.spy.antoj.judge.strategy.JudgeContext;
import com.spy.antoj.model.domain.Question;
import com.spy.antoj.model.domain.QuestionSubmit;
import com.spy.antoj.model.domain.User;
import com.spy.antoj.model.dto.question.JudgeCase;
import com.spy.antoj.model.enums.QuestionSubmitStatusEnum;
import com.spy.antoj.service.QuestionService;
import com.spy.antoj.service.QuestionSubmitService;
import com.spy.antoj.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionService questionService;

    @Resource
    private JudgeManager judgeManager;

    @Resource
    private WebSocketUtils webSocketUtils;

    @Resource
    private UserService userService;

    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1. 获取题目
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        ThrowUtils.throwIf(questionSubmit == null, ErrorCode.NOT_FOUND_ERROR);
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        // 2. 如果题目状态不是等待中，就不需要重复执行
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        // 3. 更改判题状态 运行中 - 1
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 4. 调用沙箱，获取执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        // 代码沙箱执行
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();
        // 5. 根据执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        // 6. 修改数据库中的判题信息
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionSubmitId);

        // 7. todo websocket返还信息
        User user = userService.getById(questionSubmit.getUserId());
        String toJsonStr = JSONUtil.toJsonStr(questionSubmitResult);
        webSocketUtils.sendOneMessage(user.getId().toString(), toJsonStr);
//        webSocketUtils.sendOneMessage(user.getId().toString(), JSONUtil.toJsonStr(questionSubmitResult));

        return questionSubmitResult;
    }

}
