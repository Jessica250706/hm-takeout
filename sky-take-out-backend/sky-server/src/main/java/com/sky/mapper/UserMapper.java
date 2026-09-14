package com.sky.mapper;

import com.sky.dto.UserStatisticsDTO;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 根据 openid 查询用户
     *
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入用户数据
     *
     * @param user
     */
    void insert(User user);

    /**
     * 根据 id 查询用户
     *
     * @param id
     * @return
     */
    @Select("select * from user where id = #{id}")
    User getById(Long id);

    /**
     * 统计某个时间点之前的总用户数
     *
     * @param time 时间点
     * @return 总用户数
     */
    @Select("select count(id) from user where create_time < #{time}")
    Integer getTotalUserCountBefore(LocalDateTime time);

    /**
     * 统计指定时间范围内每天新增的用户数
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 每天新增用户数列表
     */
    List<UserStatisticsDTO> getDailyNewUserCount(LocalDateTime begin, LocalDateTime end);
}
