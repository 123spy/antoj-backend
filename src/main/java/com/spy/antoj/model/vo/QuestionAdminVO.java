package com.spy.antoj.model.vo;

import cn.hutool.json.JSONUtil;
import com.spy.antoj.model.domain.Question;
import com.spy.antoj.model.dto.question.JudgeCase;
import com.spy.antoj.model.dto.question.JudgeConfig;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目
 *
 * @TableName question
 */
@Data
public class QuestionAdminVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;

    /**
     * 判题配置
     */
    private JudgeConfig judgeConfig;

    /**
     * 判题用例
     */
    private List<JudgeCase> judgeCases;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 包装类转对象
     */
    public static Question voToObj(QuestionAdminVO questionAdminVO) {
        if (questionAdminVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAdminVO, question);
        List<String> tagList = questionAdminVO.getTags();
        if (tagList != null) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        JudgeConfig voJudgeConfig = questionAdminVO.getJudgeConfig();
        if (voJudgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(voJudgeConfig));
        }
        return question;
    }

    /**
     * 对象转封包类
     */
    public static QuestionAdminVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionAdminVO questionAdminVO = new QuestionAdminVO();
        BeanUtils.copyProperties(question, questionAdminVO);
        // 标签
        List<String> tagList = JSONUtil.toList(question.getTags(), String.class);
        questionAdminVO.setTags(tagList);
        // 判题配置
        String judgeConfigStr = question.getJudgeConfig();
        questionAdminVO.setJudgeConfig(JSONUtil.toBean(judgeConfigStr, JudgeConfig.class));
        // 判题用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        questionAdminVO.setJudgeCases(judgeCaseList);
        return questionAdminVO;
    }

    private static final long serialVersionUID = 1L;
}