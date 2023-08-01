package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.DishCategory;

import java.util.List;
import java.util.Map;

public interface IDishCategoryService extends IService<DishCategory> {



    /**
     * 根据分类获取下拉列表
     * @param type
     * @return
     */
    List<Map<String,Object>> findCategoryList(Integer type);

    /**
     * 分类添加
     */
     boolean add(String name,int type);

    /**
     * 分类修改
     */
    boolean update(String id,String categoryName);

    /**
     * 分页
     */
    IPage<DishCategory> queryPage(int pageNum, int pageSize);


}
