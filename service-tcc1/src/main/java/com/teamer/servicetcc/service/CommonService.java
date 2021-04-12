package com.teamer.servicetcc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/***
* @功能描述
* @Author  zhangyj
* @Date   2020/9/16 9:13
* @Param null: 
* @return: null
* 
*/
@Service
public class CommonService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * @param key
     * @return
     */
    public String getRedisValueFor(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * @param key
     * @param value
     */
    public void saveOrUpdateRedisEntryFor(String key, String value) {
        Long expire = redisTemplate.opsForValue().getOperations().getExpire(key);
        if (expire != null && expire >= 0) {
            redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    /**
     * @param key
     * @return
     */
    public boolean removeRedisKey(String key) {
        return (!hasKey(key)) || redisTemplate.delete(key);
    }

    /**
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * @param key
     * @param value
     * @param ttl
     * @param unit
     */
    public void saveOrUpdateRedisEntryWithExpireFor(String key, String value, Long ttl, TimeUnit unit) {
        //如果过期时间设置合法
        if (ttl >= 0 && unit != null) {
            redisTemplate.opsForValue().set(key, value, ttl, unit);
        } else {
            saveOrUpdateRedisEntryFor(key, value);
        }
    }
}
