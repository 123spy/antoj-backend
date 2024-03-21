package com.spy.antoj.judge;

import cn.hutool.json.JSONUtil;
import com.spy.antoj.common.ErrorCode;
import com.spy.antoj.common.WebSocketUtils;
import com.spy.antoj.exception.BusinessException;
import com.spy.antoj.exception.ThrowUtils;
import com.spy.antoj.judge.codesandbox.CodeSandbox;
import com.spy.antoj.judge.codesandbox.CodeSandboxFactory;
import com.spy.antoj.judge.codesandbox.CodeSandboxProxy;
import com.spy.antoj.judge.codesandbox.model.DebugCodeContent;
import com.spy.antoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.spy.antoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.spy.antoj.judge.codesandbox.model.JudgeInfo;
import com.spy.antoj.judge.strategy.JudgeContext;
import com.spy.antoj.model.domain.Question;
import com.spy.antoj.model.domain.QuestionSubmit;
import com.spy.antoj.model.domain.User;
import com.spy.antoj.model.dto.QuestionDebug.QuestionDebugResult;
import com.spy.antoj.model.dto.question.JudgeCase;
import com.spy.antoj.model.enums.JudgeInfoMessageEnum;
import com.spy.antoj.model.enums.QuestionSubmitStatusEnum;
import com.spy.antoj.service.QuestionService;
import com.spy.antoj.service.QuestionSubmitService;
import com.spy.antoj.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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
        // 获取题目提交出现错误
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
        // List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        // 处理inputList
        List<String> inputList = judgeCaseList.stream().map((item) -> {
            String inputStr = item.getInput().replace("\n", " ");
            return inputStr;
        }).collect(Collectors.toList());
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
        // 如果提交成功,则更新数据库
        if (judgeInfo.getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getValue())) {
            boolean result = questionService.update()
                    .eq("id", question.getId())
                    .setSql("acceptedNum = acceptedNum + 1")
                    .update();
            ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "数据更新失败");
        }

        // 6. 修改数据库中的判题信息
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        // 如果判题机连接错误，修改结果
        if (JudgeInfoMessageEnum.REMOTE_ERROR.getText().equals(executeCodeResponse.getJudgeInfo().getMessage())) {
            judgeInfo.setMessage(JudgeInfoMessageEnum.REMOTE_ERROR.getText());
        }
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));

        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionSubmitId);

        // 7. websocket返还信息
        User user = userService.getById(questionSubmit.getUserId());
        String toJsonStr = JSONUtil.toJsonStr(questionSubmitResult);
        webSocketUtils.sendOneMessage(user.getId().toString(), toJsonStr);
//        webSocketUtils.sendOneMessage(user.getId().toString(), JSONUtil.toJsonStr(questionSubmitResult));

        return questionSubmitResult;
    }

    /**
     * 代码Debug模式
     */
    @Override
    public void doDebug(DebugCodeContent debugCodeContent) {
        // 1. 获取题目
        ThrowUtils.throwIf(debugCodeContent == null, ErrorCode.NOT_FOUND_ERROR);
        Long questionId = debugCodeContent.getQuestionId();
        Question question = questionService.getById(questionId);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);

        // 2. 调用沙箱，获取执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);

        String language = debugCodeContent.getLanguage();
        String code = debugCodeContent.getCode();

        // 获取输入用例
        List<JudgeCase> judgeCaseList = new ArrayList<>();
        String inputCaseStr = debugCodeContent.getInputCase().replace("\n", " ");

        List<String> inputList = new ArrayList<>();
        inputList.add(inputCaseStr);

        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        // 代码沙箱执行
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();
        // 5. 根据执行结果，设置题目的判题状态和信息

        QuestionDebugResult questionDebugResult = new QuestionDebugResult();
        questionDebugResult.setCode(code);
        questionDebugResult.setLanguage(language);
        questionDebugResult.setOutputList(outputList);


        // 7. websocket返还信息
        User user = userService.getById(debugCodeContent.getUserId());
        String toJsonStr = JSONUtil.toJsonStr(questionDebugResult);
        webSocketUtils.sendOneMessage(user.getId().toString(), toJsonStr);
    }

}
