package com.spy.antoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spy.antoj.common.ErrorCode;
import com.spy.antoj.exception.BusinessException;
import com.spy.antoj.model.domain.Post;
import com.spy.antoj.model.domain.PostThumb;
import com.spy.antoj.model.domain.User;
import com.spy.antoj.service.PostService;
import com.spy.antoj.service.PostThumbService;
import com.spy.antoj.mapper.PostThumbMapper;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author spy
 * @description 针对表【post_thumb(帖子点赞)】的数据库操作Service实现
 * @createDate 2024-03-07 20:59:09
 */
@Service
public class PostThumbServiceImpl extends ServiceImpl<PostThumbMapper, PostThumb>
        implements PostThumbService {

    @Resource
    private PostService postService;

    @Override
    public int doPostThumb(Long postId, User loginUser) {
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已经点赞
        long userId = loginUser.getId();
        // 非事务调用，必须使用AopContext.currentProxy()进行调用
        // 用户串行提交，需要上锁
        PostThumbService postThumbService = (PostThumbService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            int result = postThumbService.doPostThumbInner(userId, postId);
            return result;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doPostThumbInner(long userId, Long postId) {
        PostThumb postThumb = new PostThumb();
        postThumb.setPostId(postId);
        postThumb.setUserId(userId);
        QueryWrapper<PostThumb> thumbQueryWrapper = new QueryWrapper<>(postThumb);
        PostThumb oldPostThumb = this.getOne(thumbQueryWrapper);
        boolean result;
        // 已点赞
        if (oldPostThumb != null) {
            result = this.remove(thumbQueryWrapper);
            if (result) {
                // 点赞数 -1
                result = postService.update()
                        .eq("id", postId)
                        .gt("thumbNum", 0)
                        .setSql("thumbNum = thumbNum - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 未点赞
            result = this.save(postThumb);
            if (result) {
                // 点赞数 +1
                result = postService.update()
                        .eq("id", postId)
                        .setSql("thumbNum = thumbNum + 1")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }
}




