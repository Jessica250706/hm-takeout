package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDate;

public interface WorkspaceService {

    /**
     * 查询今日运营数据
     *
     * @return
     */
    BusinessDataVO getBusinessData();

    /**
     * 查询套餐总览
     *
     * @return
     */
    SetmealOverViewVO getOverViewSetmeals();

    /**
     * 查询菜品总览
     *
     * @return
     */
    DishOverViewVO getOverViewDishes();

    /**
     * 查询订单管理数据
     *
     * @return
     */
    OrderOverViewVO getOverViewOrders();

    /**
     * 查询指定日期范围内的运营数据
     *
     * @param begin 开始日期（含）
     * @param end   结束日期（含）
     */
    BusinessDataVO getBusinessData(LocalDate begin, LocalDate end);
}
