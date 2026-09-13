package com.sky.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class OrdersPaymentDTO implements Serializable {

    /**
     * 订单号
     */
    private String orderNumber;

    /**
     * 支付方式 1-微信，2-支付宝
     */
    private Integer payMethod;

}
