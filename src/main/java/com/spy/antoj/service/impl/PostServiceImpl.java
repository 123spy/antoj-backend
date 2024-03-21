package com.spy.antoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spy.antoj.common.ErrorCode;
import com.spy.antoj.constant.CommonConstant;
import com.spy.antoj.exception.BusinessException;
import com.spy.antoj.exception.ThrowUtils;
import com.spy.antoj.mapper.PostThumbMapper;
import com.spy.antoj.model.domain.Post;
import com.spy.antoj.model.domain.PostThumb;
import com.spy.antoj.model.domain.User;
import com.spy.antoj.model.dto.post.PostQueryRequest;
import com.spy.antoj.model.vo.PostVO;
import com.spy.antoj.model.vo.UserVO;
import com.spy.antoj.service.PostService;
import com.spy.antoj.mapper.PostMapper;
import com.spy.antoj.service.UserService;
import com.spy.antoj.utils.MyTextComparator;
import com.spy.antoj.utils.SqlUtils;
import javafx.util.Pair;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author spy
 * @description 针对表【post(帖子)】的数据库操作Service实现
 * @createDate 2024-03-07 20:59:04
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
        implements PostService {

    @Resource
    private UserService userService;

    @Resource
    private PostThumbMapper postThumbMapper;

    @Override
    public void validPost(Post post, boolean add) {
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = post.getTitle();
        String content = post.getContent();
        String tags = post.getTags();

        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    @Override
    public PostVO getPostVO(Post post, HttpServletRequest request) {
        PostVO postVO = PostVO.objToVo(post);
        Long postId = post.getId();
        // 1. 查询关联信息
        Long userId = post.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        postVO.setUserVO(userVO);
        // 2. 已登录，获取关联信息
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            QueryWrapper<PostThumb> postThumbQueryWrapper = new QueryWrapper<>();
            postThumbQueryWrapper.in("postId", postId);
            postThumbQueryWrapper.eq("userId", loginUser.getId());
            PostThumb postThumb = postThumbMapper.selectOne(postThumbQueryWrapper);
            postVO.setHasThumb(postThumb != null);
        }
        return postVO;
    }

    @Override
    public QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if (postQueryRequest == null) {
            return queryWrapper;
        }
        Long id = postQueryRequest.getId();
        String searchText = postQueryRequest.getSearchText();
        String title = postQueryRequest.getTitle();
        String content = postQueryRequest.getContent();
        List<String> tags = postQueryRequest.getTags();
        Long userId = postQueryRequest.getUserId();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).or().like("content", searchText).or().like("tags", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request) {
        List<Post> postList = postPage.getRecords();
        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        if (CollectionUtils.isEmpty(postList)) {
            return postVOPage;
        }
        // 查询关联信息
        Set<Long> userIdSet = postList.stream().map(post -> {
            return post.getUserId();
        }).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        // 登录，获取用户点赞
        HashMap<Long, Boolean> postIdHasThumbMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            Set<Long> postIdSet = postList.stream().map(Post::getId).collect(Collectors.toSet());
            loginUser = userService.getLoginUser(request);
            QueryWrapper<PostThumb> postThumbQueryWrapper = new QueryWrapper<>();
            postThumbQueryWrapper.in("postId", postIdSet);
            postThumbQueryWrapper.eq("userId", loginUser.getId());
            List<PostThumb> postPostThumbList = postThumbMapper.selectList(postThumbQueryWrapper);
            postPostThumbList.forEach(postPostThumb -> postIdHasThumbMap.put(postPostThumb.getPostId(), true));
        }
        // 填充信息
        List<PostVO> postVOList = postList.stream().map(post -> {
            PostVO postVO = PostVO.objToVo(post);
            Long userId = post.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            postVO.setUserVO(userService.getUserVO(user));
            postVO.setHasThumb(postIdHasThumbMap.getOrDefault(post.getId(), false));
            return postVO;
        }).collect(Collectors.toList());
        postVOPage.setRecords(postVOList);
        return postVOPage;
    }

    @Override
    public List<PostVO> matchPost(Long postId, Long num) {
        // 校验
        Post nowPost = this.getById(postId);
        ThrowUtils.throwIf(nowPost == null, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "title", "tags", "content");
        queryWrapper.isNotNull("content");
        List<Post> postList = this.list(queryWrapper);

        // 计算相似度
        List<Pair<Post, Double>> list = new ArrayList<>();
        for (int i = 0; i < postList.size(); i++) {
            Post post = postList.get(i);
            String postContent = post.getContent();

            if (StringUtils.isBlank(postContent) || nowPost.getId().equals(post.getId())) {
                continue;
            }
            // 计算分数
            Double contentSimilarity = MyTextComparator.getCosineSimilarity(nowPost.getContent(), post.getContent());
            Double tagSimilarity = MyTextComparator.getCosineSimilarity(nowPost.getTags(), post.getTags());
            Double titleSimilarity = MyTextComparator.getCosineSimilarity(nowPost.getTitle(), post.getTitle());
            Double sumSimilarity = (contentSimilarity * 0.6) + (tagSimilarity * 0.2) + (titleSimilarity * 0.2);
            list.add(new Pair<>(post, sumSimilarity));
        }

        List<Pair<Post, Double>> topPostPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> postIdList = topPostPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        postQueryWrapper.in("id", postIdList);

        Map<Long, List<PostVO>> postIdPostListMap = this.list(postQueryWrapper)
                .stream()
                .map(post -> PostVO.objToVo(post))
                .collect(Collectors.groupingBy(PostVO::getId));
        List<PostVO> finalPostList = new ArrayList<>();
        for (Long postIdItem : postIdList) {
            finalPostList.add(postIdPostListMap.get(postIdItem).get(0));
        }
        return finalPostList;
    }
}




