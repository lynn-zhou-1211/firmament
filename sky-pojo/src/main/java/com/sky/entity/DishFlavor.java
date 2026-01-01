package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 菜品口味关系表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DishFlavor {
    private static final long serialVersionUID = 1L;
    private Long id;

    private Long dishId;

    private String name;

    private String value;
}