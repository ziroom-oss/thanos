package com.ziroom.qa.quality.defende.provider.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisUtil {
    @Resource
    RedisTemplate redisTemplate;

    public void set(String key,Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置自定义的过期时间（秒）
     * @param key
     * @param value
     * @param timeout
     */
    public void setLittleScconds(String key,Object value,long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout , TimeUnit.SECONDS);
    }

    /**
     * 设置一分钟的过期时间
     * @param key
     * @param value
     */
    public void setOneMinutes(String key,Object value) {
        redisTemplate.opsForValue().set(key, value, 1 , TimeUnit.MINUTES);
    }

    /**
     * 设置一小时的过期时间
     * @param key
     * @param value
     */
    public void setOneHour(String key,Object value) {
        redisTemplate.opsForValue().set(key, value, 1 , TimeUnit.HOURS);
    }

    /**
     * 设置x小时的过期时间
     * @param key
     * @param value
     */
    public void setFewHour(String key,Object value,long hours) {
        redisTemplate.opsForValue().set(key, value, hours , TimeUnit.HOURS);
    }

    /**
     * 设置一天的过期时间
     * @param key
     * @param value
     */
    public void setOneDay(String key,Object value) {
        redisTemplate.opsForValue().set(key, value, 1 , TimeUnit.DAYS);
    }

    public Object get(String key) {
        log.info(JSON.toJSONString(key));
        return redisTemplate.opsForValue().get(key);

    }

    public Boolean deleteByKey(String key) {
        return redisTemplate.delete(key);

    }

}
