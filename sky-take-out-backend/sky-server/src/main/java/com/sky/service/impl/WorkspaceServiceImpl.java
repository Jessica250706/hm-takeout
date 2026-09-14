package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.dto.BusinessDataDTO;
import com.sky.dto.UserStatisticsDTO;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 查询今日运营数据
     *
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData() {
        LocalDate today = LocalDate.now();
        return getBusinessData(today, today);
    }

    /**
     * 查询指定日期范围内的运营数据
     *
     * @param begin 开始日期（含）
     * @param end   结束日期（含）
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData(LocalDate begin, LocalDate end) {
        // 1. 转换时间范围：begin 当天 00:00:00 到 end 后一天 00:00:00
        LocalDateTime beginTime = begin.atStartOfDay();
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();

        // 2. 查询订单汇总数据
        BusinessDataDTO businessDataDTO = orderMapper.getBusinessDataByDate(beginTime, endTime);

        double turnover = businessDataDTO.getTurnover();
        int validOrderCount = businessDataDTO.getValidOrderCount();
        int totalOrderCount = businessDataDTO.getTotalOrderCount();

        // 3. 计算订单完成率，避免除零
        double orderCompletionRate = 0.0;
        if (totalOrderCount > 0) {
            orderCompletionRate = validOrderCount * 1.0 / totalOrderCount;
        }

        // 4. 计算平均客单价，避免除零
        double unitPrice = 0.0;
        if (validOrderCount > 0) {
            unitPrice = turnover / validOrderCount;
        }

        // 5. 查询新增用户数：汇总时间段内每天的新增用户数
        List<UserStatisticsDTO> userStatisticsDTOList = userMapper.getDailyNewUserCount(beginTime, endTime);
        int newUsers = 0;
        if (userStatisticsDTOList != null && !userStatisticsDTOList.isEmpty()) {
            for (UserStatisticsDTO dto : userStatisticsDTOList) {
                if (dto.getNewUserNumber() != null) {
                    newUsers += dto.getNewUserNumber();
                }
            }
        }

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    /**
     * 查询套餐总览
     *
     * @return
     */
    @Override
    public SetmealOverViewVO getOverViewSetmeals() {
        Integer discontinued = setmealMapper.countByStatus(StatusConstant.DISABLE);
        Integer sold = setmealMapper.countByStatus(StatusConstant.ENABLE);

        return SetmealOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    /**
     * 查询菜品总览
     *
     * @return
     */
    @Override
    public DishOverViewVO getOverViewDishes() {
        Integer discontinued = dishMapper.countByStatus(StatusConstant.DISABLE);
        Integer sold = dishMapper.countByStatus(StatusConstant.ENABLE);

        return DishOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    /**
     * 查询订单管理数据
     *
     * @return
     */
    @Override
    public OrderOverViewVO getOverViewOrders() {
        Integer allOrders = orderMapper.countByStatus(null);
        Integer cancelledOrders = orderMapper.countByStatus(Orders.CANCELLED);
        Integer completedOrders = orderMapper.countByStatus(Orders.COMPLETED);
        Integer deliveredOrders = orderMapper.countByStatus(Orders.CONFIRMED);
        Integer waitingOrders = orderMapper.countByStatus(Orders.TO_BE_CONFIRMED);

        return OrderOverViewVO.builder()
                .allOrders(allOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders)
                .build();
    }
}
