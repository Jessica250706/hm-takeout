package com.sky.mapper;

import com.sky.entity.SetmealDish;
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

    /**
     * 批量插入
     *
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐 ids 批量删除对应关系
     *
     * @param setmealIds
     */
    void deleteBySetmealIds(List<Long> setmealIds);
}
