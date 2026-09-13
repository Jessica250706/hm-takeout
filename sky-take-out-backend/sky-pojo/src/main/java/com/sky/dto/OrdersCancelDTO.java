package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrdersCancelDTO implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 订单取消原因
     */
    private String cancelReason;

}
