package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BusinessDataDTO implements Serializable {

    /**
     * 营业额
     */
    private Double turnover;

    /**
     * 有效订单数
     */
    private Integer validOrderCount;

    /**
     * 总订单数
     */
    private Integer totalOrderCount;
}
