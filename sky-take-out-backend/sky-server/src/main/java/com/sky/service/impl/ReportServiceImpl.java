package com.sky.service.impl;

import com.sky.dto.TurnoverDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 统计指定区间时间内的营业额
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 计算日期
        List<LocalDate> dateList = new ArrayList<>();

        LocalDate current = begin;
        while (!current.isAfter(end)) {
            dateList.add(current);
            current = current.plusDays(1);
        }

        // 计算营业额：状态为已完成的订单金额合计
        List<TurnoverDTO> turnoverList = orderMapper.getTurnoverByDate(begin, end, Orders.COMPLETED);

        // 转成 Map<LocalDate, BigDecimal>，方便按日期查找
        Map<LocalDate, BigDecimal> turnoverMap = new HashMap<>();
        for (TurnoverDTO dto : turnoverList) {
            LocalDate date = LocalDate.parse(dto.getDate());
            BigDecimal turnover = dto.getTurnover() == null ? BigDecimal.ZERO : dto.getTurnover();
            turnoverMap.put(date, turnover);
        }

        // 按日期顺序补全营业额，没有订单的日期填 0
        List<BigDecimal> turnoverListResult = new ArrayList<>();
        for (LocalDate date : dateList) {
            BigDecimal turnover = turnoverMap.getOrDefault(date, BigDecimal.ZERO);
            turnoverListResult.add(turnover);
        }

        // 拼接日期字符串和营业额字符串
        String dateStr = dateList.stream()
                .map(LocalDate::toString)
                .collect(Collectors.joining(","));
        String turnoverStr = turnoverListResult.stream()
                .map(BigDecimal::toString)
                .collect(Collectors.joining(","));

        return TurnoverReportVO.builder()
                .dateList(dateStr)
                .turnoverList(turnoverStr)
                .build();
    }
}
