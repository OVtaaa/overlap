package com.ov.schedule;


import com.ov.constant.UserConstant;
import com.ov.pojo.User;
import com.ov.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CacheRecommend {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 定时缓存公共用户的首页信息
     */
    @Scheduled(fixedDelay = UserConstant.RECOMMEND_USER_SCHEDULE_TIME)
    public void cacheUserRecommend()  {
        userService.cacheUserRecommend();
    }

//    public void cacheHotUserRecommend() {
//        RLock lock = redissonClient.getLock(UserConstant.RECOMMEND_HOT_USER_LOCK);
//        try {
//            /*
//              tryLock 参数：最大等待时间（由于只需要执行一次，所以置为 0 不需要等待）
//              锁自动释放时间（避免出现执行时间过长，锁提前释放的可能，采用看门狗机制，不手动设置释锁时间）
//              时间单位
//             */
//            boolean isLock = lock.tryLock(0, TimeUnit.SECONDS);
//            if(isLock) {
//                //  按 redis 中缓存的热用户数据 更新热用户的首页信息
//                List<String> range = stringRedisTemplate.opsForList().range(UserConstant.HOT_USER_KEY, 0, -1);
//                if (CollectionUtils.isEmpty(range))
//                    return;
//                for (String idStr : range) {
//                    List<User> userList = userService.getRecommendHotUserBySQL(Long.parseLong(idStr));
//                    userService.saveUserListToRedis(userList, UserConstant.RECOMMEND_HOT_USER_KEY + ":" + idStr);
//                }
//            }
//        } catch (Exception exception) {
//            log.error("schedule error",exception);
//        } finally {
//            //  释放锁
//            if(lock!=null && lock.isHeldByCurrentThread())
//                lock.unlock();
//        }
//    }
//
//    public void cacheCommonUserRecommend()  {
//        RLock lock = redissonClient.getLock(UserConstant.RECOMMEND_COMMON_USER_LOCK);
//        try {
//            /*
//              tryLock 参数：最大等待时间（由于只需要执行一次，所以置为 0 不需要等待）
//              锁自动释放时间（避免出现执行时间过长，锁提前释放的可能，采用看门狗机制，不手动设置释锁时间）
//              时间单位
//             */
//            boolean isLock = lock.tryLock(0, TimeUnit.SECONDS);
//            if(isLock) {
//                //  更新 redis 首页展示数据
//                List<User> userList = userService.getRecommendCommonUserBySQL();
//                userService.saveUserListToRedis(userList,UserConstant.RECOMMEND_COMMON_USER_KEY);
//            }
//        } catch (InterruptedException e) {
//            log.error("schedule error", e);
//        } finally {
//            //  释放锁
//            if(lock!=null && lock.isHeldByCurrentThread())
//                lock.unlock();
//        }
//    }
}
