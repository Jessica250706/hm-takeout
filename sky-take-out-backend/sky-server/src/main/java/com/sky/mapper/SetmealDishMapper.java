package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品 id 查询对应的套餐 id
     *
     * @param dishIds
     * @return
     */
    List<Long> getSetmealByDishId(List<Long> dishIds);
}
