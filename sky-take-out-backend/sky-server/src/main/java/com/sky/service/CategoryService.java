package com.sky.service;

import com.sky.dto.CategoryDTO;

public interface CategoryService {

    /**
     * 新增分类
     *
     * @param categoryDTO
     * @return
     */
    void addCategory(CategoryDTO categoryDTO);

    /**
     * 修改分类
     *
     * @param categoryDTO
     * @return
     */
    void updateCategory(CategoryDTO categoryDTO);

    /**
     * 根据 id 删除分类
     *
     * @param id
     * @return
     */
    void deleteCategory(Long id);
}
