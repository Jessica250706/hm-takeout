package com.sky.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TurnoverDTO {

    /**
     * 日期，格式 yyyy-MM-dd
     */
    private String date;

    /**
     * 营业额
     */
    private BigDecimal turnover;
}
