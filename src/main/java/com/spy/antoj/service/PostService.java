package com.spy.antoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spy.antoj.model.domain.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spy.antoj.model.dto.post.PostQueryRequest;
import com.spy.antoj.model.vo.PostVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author spy
 * @description 针对表【post(帖子)】的数据库操作Service
 * @createDate 2024-03-07 20:59:04
 */
public interface PostService extends IService<Post> {

    /**
     * 参数校验
     *
     * @param post
     * @param add
     */
    void validPost(Post post, boolean add);

    /**
     * 获取Post视图
     *
     * @param post
     * @param request
     * @return
     */
    PostVO getPostVO(Post post, HttpServletRequest request);

    /**
     * 获取查询条件
     *
     * @param postQueryRequest
     * @return
     */
    QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest);

    /**
     * 分页获取帖子封装
     *
     * @param postPage
     * @param request
     */
    Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request);

    /**
     * 帖子匹配
     *
     * @param postId
     * @param num
     * @return
     */
    List<PostVO> matchPost(Long postId, Long num);
}
