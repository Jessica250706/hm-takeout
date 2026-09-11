package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ShoppingCartDTO implements Serializable {

    /**
     * 菜品 id
     */
    private Long dishId;

    /**
     * 套餐 id
     */
    private Long setmealId;

    /**
     * 口味
     */
    private String dishFlavor;

}
