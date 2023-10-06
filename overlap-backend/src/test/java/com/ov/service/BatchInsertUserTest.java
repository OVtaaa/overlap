package com.ov.service;
import java.util.Date;

import com.ov.constant.UserConstant;
import com.ov.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
public class BatchInsertUserTest {

    @Autowired
    private UserService userService;

    /**
     * 异步批量添加数据
     * 结果：
     *  插入十万条 4.362 秒
     *  插入百万条 30.966 秒
     */
//    @Test
//    void batchInsertUser(){
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//
//        //  做十组并发，每组并发 十万条数据
//        int groupSize = 10;
//        int groupInsertData = 100000;
//        int batchSize = 10000;
//        List<CompletableFuture<Void>> task = new ArrayList<>();
//        for(int i = 0; i < groupSize; i++) {
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                List<User> list = new ArrayList<>();
//                for (int j = 0; j < groupInsertData; j++) {
//                    User user = new User();
//                    user.setUserAccount("ovMock");
//                    user.setPassword("123456");
//                    user.setUsername("ovMock" + j);
//                    user.setGender("男");
//                    user.setAge(20);
//                    user.setIntroduction("爱睡觉");
//                    user.setAvatarUrl(UserConstant.DEFAULT_AVATAR_URL);
//                    user.setPhone("10086");
//                    user.setEmail("12138@qq.com");
//                    user.setUserRole(0);
//                    user.setIsPost(1);
//                    user.setUserStatus(0);
//                    user.setCreateTime(new Date());
//                    user.setUpdateTime(new Date());
//                    user.setTags("[\"王者荣耀\"]");
//                    user.setIsDelete(0);
//                    list.add(user);
//                }
//                userService.saveBatch(list, batchSize);
//            });
//            task.add(future);
//        }
//        CompletableFuture.allOf(task.toArray(new CompletableFuture[]{})).join();
//        stopWatch.stop();
//        System.out.println(stopWatch.getTotalTimeMillis());
//    }
}
