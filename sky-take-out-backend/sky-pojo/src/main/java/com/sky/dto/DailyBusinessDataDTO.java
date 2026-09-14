package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DailyBusinessDataDTO implements Serializable {

    /**
     * 日期，格式 yyyy-MM-dd
     */
    private String date;

    /**
     * 营业额（已完成订单总金额）
     */
    private BigDecimal turnover;

    /**
     * 有效订单数（已完成订单数）
     */
    private Integer validOrderCount;

    /**
     * 总订单数
     */
    private Integer totalOrderCount;

    /**
     * 订单完成率（小数，如 0.85）
     */
    private Double orderCompletionRate;

    /**
     * 平均客单价
     */
    private Double unitPrice;

    /**
     * 新增用户数
     */
    private Integer newUsers;
}
