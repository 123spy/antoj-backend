package com.spy.antoj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spy.antoj.common.BaseResponse;
import com.spy.antoj.common.ErrorCode;
import com.spy.antoj.common.ResultUtils;
import com.spy.antoj.exception.BusinessException;
import com.spy.antoj.exception.ThrowUtils;
import com.spy.antoj.model.domain.Post;
import com.spy.antoj.model.domain.PostThumb;
import com.spy.antoj.model.domain.Question;
import com.spy.antoj.model.domain.User;
import com.spy.antoj.model.dto.post.PostQueryRequest;
import com.spy.antoj.model.dto.postthumb.PostThumbAddRequest;
import com.spy.antoj.model.dto.question.QuestionQueryRequest;
import com.spy.antoj.model.vo.PostVO;
import com.spy.antoj.model.vo.QuestionVO;
import com.spy.antoj.model.vo.UserVO;
import com.spy.antoj.service.PostService;
import com.spy.antoj.service.PostThumbService;
import com.spy.antoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post_thumb")
@Slf4j
public class PostThumbController {

    @Resource
    private UserService userService;

    @Resource
    private PostThumbService postThumbService;

    @Resource
    private PostService postService;

    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest, HttpServletRequest request) {
        if (postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 点赞需要登录
        User loginUser = userService.getLoginUser(request);
        Long postId = postThumbAddRequest.getPostId();
        int result = postThumbService.doPostThumb(postId, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/list/vo")
    public BaseResponse<List<PostVO>> listPostThumbByList(@RequestBody PostQueryRequest postQueryRequest, HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = postQueryRequest.getUserId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<PostThumb> postThumbQueryWrapper = new QueryWrapper<>();
        postThumbQueryWrapper.eq("userId", userId);
        Set<Long> postIdSet = postThumbService.list(postThumbQueryWrapper).stream().map(item -> {
            Long postId = item.getPostId();
            return postId;
        }).collect(Collectors.toSet());

        if (postIdSet.size() > 0) {
            QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
            postQueryWrapper.in("id", postIdSet);
            List<PostVO> postVOList = new ArrayList<>();
            List<Post> postList = postService.list(postQueryWrapper);
            if (postList.size() > 0) {
                postVOList = postList.stream().map(item -> {
                    User user = userService.getById(item.getUserId());
                    UserVO userVO = userService.getUserVO(user);
                    PostVO postVO = PostVO.objToVo(item);
                    postVO.setUserVO(userVO);
                    return postVO;
                }).collect(Collectors.toList());
            }
            return ResultUtils.success(postVOList);
        }

        return ResultUtils.success(new ArrayList<>());
    }
}
