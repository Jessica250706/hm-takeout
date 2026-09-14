package com.sky.dto;

import lombok.Data;

@Data
public class UserStatisticsDTO {

    /**
     * 日期，格式 yyyy-MM-dd
     */
    private String date;

    /**
     * 新增用户数量
     */
    private Integer newUserNumber;
}