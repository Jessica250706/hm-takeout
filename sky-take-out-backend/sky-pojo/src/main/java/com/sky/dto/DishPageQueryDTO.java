package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DishPageQueryDTO implements Serializable {

    /**
     * 页码
     */
    private int page;

    /**
     * 每页记录数
     */
    private int pageSize;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 菜品分类 id
     */
    private Integer categoryId;

    /**
     * 0-停售；1-起售
     */
    private Integer status;

}
