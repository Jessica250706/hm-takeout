package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.*;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     *
     * @param orders
     * @return
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     *
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据 id 查询订单信息
     *
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id};")
    Orders getById(Long id);

    /**
     * 分页查询订单数据
     *
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据订单状态和下单时间查询订单
     *
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime orderTime);

    /**
     * 统计指定日期范围内每天的营业额
     *
     * @param begin  开始日期
     * @param end    结束日期
     * @param status 订单状态
     * @return 每天的营业额列表
     */
    List<TurnoverDTO> getTurnoverByDate(LocalDate begin, LocalDate end, Integer status);

    /**
     * 统计指定时间范围内每天的订单数据
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 每天的总订单数和有效订单数
     */
    List<OrderStatisticsDTO> getOrderStatisticsByDate(LocalDateTime begin, LocalDateTime end);

    /**
     * 统计指定时间范围内的订单数据
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 每天的总订单数和有效订单数
     */
    BusinessDataDTO getBusinessDataByDate(LocalDateTime begin, LocalDateTime end);

    /**
     * 根据订单状态查询订单的数量
     *
     * @param status
     * @return
     */
    Integer countByStatus(Integer status);

    /**
     * 统计指定时间范围内的运营数据
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 每天的总订单数和有效订单数
     */
    List<DailyBusinessDataDTO> getDailyBusinessData(LocalDateTime begin, LocalDateTime end);

}
