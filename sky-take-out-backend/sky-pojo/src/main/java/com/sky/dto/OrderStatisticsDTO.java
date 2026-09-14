package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderStatisticsDTO implements Serializable {

    /**
     * 日期，格式 yyyy-MM-dd
     */
    private String date;

    /**
     * 当天总订单数
     */
    private Integer totalOrderCount;

    /**
     * 当天有效订单数
     */
    private Integer validOrderCount;
}