package com.sky.task;

import com.sky.constant.MessageConstant;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类：定时处理订单状态
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ?") // 每分钟触发一次
    public void processTimeoutOrder() {
        LocalDateTime time = LocalDateTime.now();
        log.info("定时处理超时订单：{}", time);

        List<Orders> ordersList = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, time.plusMinutes(-15));

        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelTime(time);
                orders.setCancelReason(MessageConstant.ORDER_TIMEOUT);
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 处理一直处于派送中的订单
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨两点触发一次
    public void processDeliveryOrder() {
        LocalDateTime time = LocalDateTime.now();
        log.info("定时处理处于派送中的订单：{}", time);

        List<Orders> ordersList = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, time.minusHours(2));

        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }

}
