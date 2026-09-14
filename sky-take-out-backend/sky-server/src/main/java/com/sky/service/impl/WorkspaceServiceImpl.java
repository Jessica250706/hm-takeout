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
        // 获取今日日期范围
        LocalDate date = LocalDate.now();
        LocalDateTime begin = date.atStartOfDay();
        LocalDateTime end = begin.plusDays(1);

        // 查询订单数据
        BusinessDataDTO businessDataDTO = orderMapper.getBusinessDataByDate(begin, end);

        if (businessDataDTO == null) {
            businessDataDTO = new BusinessDataDTO();
        }

        // 使用 BigDecimal 或 Double 时进行判空
        double turnover = businessDataDTO.getTurnover() == null ? 0.0 : businessDataDTO.getTurnover();
        int validOrderCount = businessDataDTO.getValidOrderCount() == null ? 0 : businessDataDTO.getValidOrderCount();
        int totalOrderCount = businessDataDTO.getTotalOrderCount() == null ? 0 : businessDataDTO.getTotalOrderCount();

        // 计算订单完成率，避免除零
        double orderCompletionRate = 0.0;
        if (totalOrderCount > 0) {
            orderCompletionRate = validOrderCount * 1.0 / totalOrderCount;
        }

        // 4计算平均客单价，避免除零
        double unitPrice = 0.0;
        if (validOrderCount > 0) {
            unitPrice = turnover / validOrderCount;
        }

        // 查询新增用户数
        List<UserStatisticsDTO> userStatisticsDTOList = userMapper.getDailyNewUserCount(begin, end);
        int newUsers = 0;
        if (userStatisticsDTOList != null && !userStatisticsDTOList.isEmpty()) {
            newUsers = userStatisticsDTOList.get(0).getNewUserNumber() == null ? 0 : userStatisticsDTOList.get(0).getNewUserNumber();
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
