package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单明细数据
     *
     * @param orderDetailList
     */
    void insertBatch(List<OrderDetail> orderDetailList);

    /**
     * 根据订单 id 获取订单详情信息
     *
     * @param orderId
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId};")
    List<OrderDetail> listByOrderId(Long orderId);

    /**
     * 统计指定时间范围内销量 TOP 10 的菜品
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 销量 TOP 10 列表
     */
    List<GoodsSalesDTO> getTop10ByDate(LocalDateTime begin, LocalDateTime end);
}
