package com.spy.antoj.controller;

import com.spy.antoj.common.BaseResponse;
import com.spy.antoj.common.ErrorCode;
import com.spy.antoj.common.ResultUtils;
import com.spy.antoj.exception.BusinessException;
import com.spy.antoj.model.domain.User;
import com.spy.antoj.model.dto.postthumb.PostThumbAddRequest;
import com.spy.antoj.service.PostThumbService;
import com.spy.antoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/post_thumb")
@Slf4j
public class PostThumbController {

    @Resource
    private UserService userService;

    @Resource
    private PostThumbService postThumbService;

    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest, HttpServletRequest request) {
        if(postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 点赞需要登录
        User loginUser = userService.getLoginUser(request);
        Long postId = postThumbAddRequest.getPostId();
        int result = postThumbService.doPostThumb(postId, loginUser);
        return ResultUtils.success(result);
    }
}
