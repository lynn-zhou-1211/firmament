package com.sky.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeUtils {

    // 机器ID (0-31)
    private static final long WORKER_ID = 1;
    // 数据中心ID (0-31)
    private static final long DATACENTER_ID = 1;

    // Hutool 提供的雪花算法对象
    private static final Snowflake snowflake = IdUtil.getSnowflake(WORKER_ID, DATACENTER_ID);

    /**
     * 生成下一个唯一的 ID
     * @return String 类型的 ID (为了兼容你的 order.setNumber)
     */
    public static String nextIdStr() {
        return snowflake.nextIdStr();
    }

    /**
     * 生成下一个唯一的 ID (Long 类型)
     */
    public static long nextId() {
        return snowflake.nextId();
    }
}