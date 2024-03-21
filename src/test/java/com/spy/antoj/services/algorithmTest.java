package com.spy.antoj.services;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import com.spy.antoj.model.domain.Post;
import com.spy.antoj.service.PostService;
import com.spy.antoj.utils.MyTextComparator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class algorithmTest {

//    public static void main(String[] args) {
//        String txtLeft = "我是一只猫";
//        String txtRight = "我是一只狗";
//        double cosVal = computeTxtSimilar(txtLeft, txtRight);
//        System.out.println("余弦值：" + cosVal);
//    }

    @Resource
    private PostService postService;

    @Test
    public void test06() {
//        List<String> stringList = Arrays.asList("我在吉林大学学习计算机，计算机编程十分有趣。",
//                "我在JiLin University学习编程，我认为编程有难度",
//                "今天天气很好，我要出去走走。",
//                "我在家里蹲大学学习计算机，计算机编程十分有趣。");
//
//
//        int size = stringList.size();
//        for (int i = 0; i < size; i++) {
//            for (int j = i + 1; j < size; j++) {
//                System.out.println("文本" + i + "和文本" + j + "的相似度为。"
//                        + MyTextComparator.getCosineSimilarity(stringList.get(i), stringList.get(j)) * 100 + "%");
//            }
//        }


        Post post1 = postService.getById(Long.valueOf("1769702001504968706"));
        Post post2 = postService.getById(Long.valueOf("1770266561462964226"));
        System.out.println("文本 post1" + "和文本 post2" + "的相似度为。"
                + MyTextComparator.getCosineSimilarity(post1.getContent(), post2.getContent()) * 100 + "%");
    }
}
