package com.sky.vo;

import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true) // 保证 toString 和 equals 包含父类字段
public class DishVO extends Dish implements Serializable { // 👈 核心：继承 Dish

    // 分类名称 (联表查询)
    private String categoryName;

    // 菜品关联的口味 (联表查询)
    private List<DishFlavor> flavors = new ArrayList<>();

}