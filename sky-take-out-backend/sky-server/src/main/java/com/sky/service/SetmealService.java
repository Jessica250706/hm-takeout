package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新增套餐及其包含的菜品
     *
     * @param setmealDTO
     * @return
     */
    void addSetmealWithDishes(SetmealDTO setmealDTO);

    /**
     * 批量删除套餐
     *
     * @param ids
     * @return
     */
    void deleteBatchSetmeal(List<Long> ids);

    /**
     * 根据 id 查询套餐和套餐菜品关系
     *
     * @param id
     * @return
     */
    SetmealVO getByIdWithDish(Long id);

    /**
     * 修改套餐
     *
     * @param setmealDTO
     * @return
     */
    void updateSetmeal(SetmealDTO setmealDTO);

    /**
     * 套餐起售、停售
     *
     * @param status
     * @param id
     * @return
     */
    void updateSetmealStatus(Integer status, Long id);

    /**
     * 根据分类 id 查询套餐
     *
     * @param categoryId
     * @return
     */
    List<Setmeal> listByCategoryId(Long categoryId);

    /**
     * 根据套餐 id 查询包含的菜品
     *
     * @param id
     * @return
     */
    List<DishItemVO> listDishItemById(Long id);
}
