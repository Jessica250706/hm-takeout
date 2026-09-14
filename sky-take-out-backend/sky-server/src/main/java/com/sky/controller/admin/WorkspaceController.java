package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("AdminWorkspaceController")
@RequestMapping("/admin/workspace")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 查询今日运营数据
     *
     * @return
     */
    @GetMapping("/businessData")
    public Result<BusinessDataVO> businessData() {
        return Result.success(workspaceService.getBusinessData());
    }

    /**
     * 查询套餐总览
     *
     * @return
     */
    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> overViewSetmeals() {
        return Result.success(workspaceService.getOverViewSetmeals());
    }

    /**
     * 查询菜品总览
     *
     * @return
     */
    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> overViewDishes() {
        return Result.success(workspaceService.getOverViewDishes());
    }

    /**
     * 查询订单管理数据
     *
     * @return
     */
    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> overViewOrders() {
        return Result.success(workspaceService.getOverViewOrders());
    }
}
