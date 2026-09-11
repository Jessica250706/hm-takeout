package com.sky.controller.admin;

import com.sky.constant.CacheConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@RestController("AdminSetmealController")
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增套餐
     *
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @CacheEvict(cacheNames = CacheConstant.SETMEAL_CACHE, key = "#setmealDTO.categoryId")
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO) {
        setmealService.addSetmealWithDishes(setmealDTO);
        return Result.success();
    }

    /**
     * 批量删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(cacheNames = CacheConstant.SETMEAL_CACHE, allEntries = true)
    public Result deleteSetmeal(@RequestParam List<Long> ids) {
        setmealService.deleteBatchSetmeal(ids);
        return Result.success();
    }

    /**
     * 根据 id 查询套餐
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealVO> getSetmealById(@PathVariable Long id) {
        SetmealVO setmealVO = setmealService.getByIdWithDish(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @CacheEvict(cacheNames = CacheConstant.SETMEAL_CACHE, allEntries = true)
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO) {
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐起售、停售
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(cacheNames = CacheConstant.SETMEAL_CACHE, allEntries = true)
    public Result updateSetmealStatus(@PathVariable Integer status, Long id) {
        setmealService.updateSetmealStatus(status, id);
        return Result.success();
    }

}
