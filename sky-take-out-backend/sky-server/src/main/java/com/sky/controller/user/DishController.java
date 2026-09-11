package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("UserDishController")
@RequestMapping("/user/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    public Result<List<DishVO>> listByCategoryId(Long categoryId) {
        // 构造 redis 中的 key，规则：dish_{categoryId}
        String key = "dish_" + categoryId;

        // 查询 redis 中是否存在菜品数据
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (list != null && !list.isEmpty()) {
            // 如果存在，直接返回，无需查询数据库
            return Result.success(list);
        }

        // 如果不存在，查询数据库，将查询到的数据放入 redis 中
        List<DishVO> dishVO = dishService.listDishVOByCategoryId(categoryId);

        redisTemplate.opsForValue().set(key, dishVO);

        return Result.success(dishVO);
    }
}
