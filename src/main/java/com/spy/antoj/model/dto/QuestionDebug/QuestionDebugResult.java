package com.spy.antoj.model.dto.QuestionDebug;


import lombok.Data;

import java.util.List;

@Data
public class QuestionDebugResult {

    private String code;

    private String language;

    private List<String> outputList;
}
