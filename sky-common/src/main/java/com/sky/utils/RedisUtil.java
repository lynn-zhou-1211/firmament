package com.sky.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // ============================ String 基础操作 ============================

    /**
     * 写入 Redis
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        if (value == null) return;
        String jsonString = value instanceof String ? (String) value : JSON.toJSONString(value);
        stringRedisTemplate.opsForValue().set(key, jsonString);
    }

    /**
     * 写入 Redis 并设置过期时间
     * @param key
     * @param value
     * @param time
     * @param timeUnit
     */
    public void set(String key, Object value, long time, TimeUnit timeUnit) {
        if (value == null) return;
        String jsonString = value instanceof String ? (String) value : JSON.toJSONString(value);
        stringRedisTemplate.opsForValue().set(key, jsonString, time, timeUnit);
    }

    /**
     * 读取 Redis (单个对象)
     * @param key
     * @param clazz
     * @return
     */
    public <T> T get(String key, Class<T> clazz) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.hasLength(json)) {
            return null;
        }
        // 如果是 String 类型，直接返回，不用反序列化
        if (clazz.equals(String.class)) {
            return (T) json;
        }
        return JSON.parseObject(json, clazz);
    }

    /**
     * 读取 Redis (List集合)
     * @param key
     * @param clazz
     * @return
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.hasLength(json)) {
            return null;
        }
        return JSON.parseArray(json, clazz);
    }

    /**
     * 删除 Key
     * @param key
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 批量删除 Key (支持通配符)
     * @param pattern 比如 "dish_*"
     */
    public void deleteByPattern(String pattern) {
        stringRedisTemplate.delete(stringRedisTemplate.keys(pattern));
    }

    // ============================ Hash 操作 (进阶) ============================
    // 如果你要做购物车缓存，或者存复杂的对象属性，会用到这些

    /**
     * 往 Hash 结构中存入数据
     * @param key 大Key
     * @param field 小Key (字段名)
     * @param value 值
     */
    public void hSet(String key, String field, Object value) {
        if (value == null) return;
        String jsonString = value instanceof String ? (String) value : JSON.toJSONString(value);
        stringRedisTemplate.opsForHash().put(key, field, jsonString);
    }

    /**
     * 从 Hash 结构中获取数据
     */
    public <T> T hGet(String key, String field, Class<T> clazz) {
        Object val = stringRedisTemplate.opsForHash().get(key, field);
        if (val == null) return null;
        String json = (String) val;
        return JSON.parseObject(json, clazz);
    }

    /**
     * 获取 Hash 结构中所有的键值对
     * 返回 Map<String, String>，需要自己再处理一下反序列化，或者根据需要封装
     */
    // 暂时用不到，先不写，避免代码太复杂
}