package com.sky.constant;

public class RedisConstant {

    // 店铺营业状态 key
    public static final String KEY_SHOP_STATUS = "shop:status";

    // 菜品缓存前缀 (注意这里带上了冒号)
    public static final String KEY_DISH_PREFIX = "dish:";

    // 套餐缓存前缀
    public static final String KEY_SETMEAL_PREFIX = "setmeal:";

    // 所有的 Key 通配符 (用于批量删除)
    public static final String PATTERN_DISH = "dish:*";
    public static final String PATTERN_SETMEAL = "setmeal:*";
}