package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserStatisticsDTO implements Serializable {

    /**
     * 日期，格式 yyyy-MM-dd
     */
    private String date;

    /**
     * 新增用户数量
     */
    private Integer newUserNumber;
}