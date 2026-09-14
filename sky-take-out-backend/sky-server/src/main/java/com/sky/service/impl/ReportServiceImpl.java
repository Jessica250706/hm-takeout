package com.sky.service.impl;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 统计指定区间时间内的营业额
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    @Transactional
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 计算日期
        List<LocalDate> dateList = getRangeTime(begin, end);

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

    /**
     * 统计指定区间时间内的用户
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    @Transactional
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 计算日期
        List<LocalDate> dateList = getRangeTime(begin, end);

        // 查询 begin 之前的总用户数（基准）
        LocalDateTime beginTime = begin.atStartOfDay();
        Integer baseTotal = userMapper.getTotalUserCountBefore(beginTime);
        if (baseTotal == null) {
            baseTotal = 0;
        }

        // 查询 begin 到 end 之间每天新增的用户数
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();
        List<UserStatisticsDTO> dailyList = userMapper.getDailyNewUserCount(beginTime, endTime);

        // 把新增用户数转成 Map<LocalDate, Integer>，方便按日期查找
        Map<LocalDate, Integer> newUserMap = new HashMap<>();
        for (UserStatisticsDTO dto : dailyList) {
            LocalDate date = LocalDate.parse(dto.getDate());
            newUserMap.put(date, dto.getNewUserNumber());
        }

        // 按日期顺序累加，得到每天的总用户数和新增用户数
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        int cumulative = baseTotal;

        for (LocalDate date : dateList) {
            Integer newUser = newUserMap.getOrDefault(date, 0);
            cumulative += newUser;

            totalUserList.add(cumulative);
            newUserList.add(newUser);
        }

        // 拼接字符串
        String dateStr = dateList.stream()
                .map(LocalDate::toString)
                .collect(Collectors.joining(","));
        String totalUserStr = totalUserList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String newUserStr = newUserList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return UserReportVO.builder()
                .dateList(dateStr)
                .totalUserList(totalUserStr)
                .newUserList(newUserStr)
                .build();
    }

    /**
     * 统计指定区间时间内的订单
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    @Transactional
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 计算日期
        List<LocalDate> dateList = getRangeTime(begin, end);

        // 查询每天的订单统计
        LocalDateTime beginTime = begin.atStartOfDay();
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();
        List<OrderStatisticsDTO> orderStatisticsDTOList = orderMapper.getOrderStatisticsByDate(beginTime, endTime);

        // 转成 Map<LocalDate, OrderStatisticsDTO>，方便按日期查找
        Map<LocalDate, OrderStatisticsDTO> statisticsMap = new HashMap<>();
        for (OrderStatisticsDTO dto : orderStatisticsDTOList) {
            LocalDate date = LocalDate.parse(dto.getDate());
            statisticsMap.put(date, dto);
        }

        // 按日期顺序补全数据，没有订单的日期填 0
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        int totalOrderCount = 0;
        int validOrderCount = 0;

        for (LocalDate date : dateList) {
            OrderStatisticsDTO dto = statisticsMap.get(date);
            int orderCount = 0;
            int validCount = 0;
            if (dto != null) {
                orderCount = dto.getTotalOrderCount() == null ? 0 : dto.getTotalOrderCount();
                validCount = dto.getValidOrderCount() == null ? 0 : dto.getValidOrderCount();
            }
            orderCountList.add(orderCount);
            validOrderCountList.add(validCount);
            totalOrderCount += orderCount;
            validOrderCount += validCount;
        }
        // 计算订单完成率，注意除零
        double orderCompletionRate = 0.0;
        if (totalOrderCount > 0) {
            orderCompletionRate = (double) validOrderCount / totalOrderCount;
        }

        // 拼接字符串
        String dateStr = dateList.stream()
                .map(LocalDate::toString)
                .collect(Collectors.joining(","));
        String orderCountStr = orderCountList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String validOrderCountStr = validOrderCountList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // 构建返回对象
        return OrderReportVO.builder()
                .dateList(dateStr)
                .orderCountList(orderCountStr)
                .validOrderCountList(validOrderCountStr)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }


    /**
     * 统计指定区间时间内的销量 TOP 10 的菜品
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getTopTenOrdersStatistics(LocalDate begin, LocalDate end) {
        // 构建时间范围
        LocalDateTime beginTime = begin.atStartOfDay();
        LocalDateTime endTime = end.plusDays(1).atStartOfDay();

        // 查询销量 TOP 10
        List<GoodsSalesDTO> top10List = orderDetailMapper.getTop10ByDate(beginTime, endTime);

        // 拼接名称和销量字符串
        String nameList = top10List.stream()
                .map(GoodsSalesDTO::getName)
                .collect(Collectors.joining(","));
        String numberList = top10List.stream()
                .map(dto -> String.valueOf(dto.getNumber()))
                .collect(Collectors.joining(","));

        // 返回结果
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    /**
     * 获取从开始日期到结束日期之间所有日期的列表
     *
     * @param begin
     * @param end
     * @return
     */
    private List<LocalDate> getRangeTime(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate current = begin;
        while (!current.isAfter(end)) {
            dateList.add(current);
            current = current.plusDays(1);
        }
        return dateList;
    }

    /**
     * 导出运营数据报表
     *
     * @return
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        // Step1.查询数据库，获取 30 天内的营业数据
        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate beginDate = endDate.minusDays(29);
        BusinessDataVO businessData = workspaceService.getBusinessData(beginDate, endDate);

        // Step2.通过 POI 将数据写入到 Excel 文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            // 基于模版文件创建一个新的 Excel 文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            // 获取表格文件的 Sheet 页
            XSSFSheet sheet = excel.getSheetAt(0);

            // 填充数据：时间
            sheet.getRow(1).getCell(1).setCellValue("时间" + beginDate + "至" + endDate);

            // 获得第四行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());

            // 获得第五航
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

            // 获取第八行到第三十七行
            LocalDateTime beginTime = beginDate.atStartOfDay();
            LocalDateTime endTime = endDate.plusDays(1).atStartOfDay();
            List<DailyBusinessDataDTO> dailyBusinessDataDTOList = orderMapper.getDailyBusinessData(beginTime, endTime);
            for (int i = 0; i < 30; i++) {
                row = sheet.getRow(7 + i);
                DailyBusinessDataDTO businessDataDTO = dailyBusinessDataDTOList.get(i);
                if (businessDataDTO != null) {
                    row.getCell(1).setCellValue(String.valueOf(beginDate.plusDays(i))); // 日期
                    row.getCell(2).setCellValue(businessDataDTO.getTurnover().toString()); // 营业额
                    row.getCell(3).setCellValue(businessDataDTO.getValidOrderCount()); // 有效订单
                    row.getCell(4).setCellValue(businessDataDTO.getOrderCompletionRate()); // 订单完成率
                    row.getCell(5).setCellValue(businessDataDTO.getUnitPrice()); // 平均客单价
                    row.getCell(6).setCellValue(businessDataDTO.getNewUsers()); // 新增用户数
                }
            }

            // Step3.通过输出流将 Excel 文件下载到客户端
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            // 关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
