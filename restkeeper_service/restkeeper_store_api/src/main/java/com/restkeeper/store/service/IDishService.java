package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.Dish;
import com.restkeeper.store.entity.DishFlavor;

import java.util.List;
import java.util.Map;

public interface IDishService extends IService<Dish> {

    boolean save(Dish dish, List<DishFlavor> flavorList);

    boolean update(Dish dish, List<DishFlavor> flavorList);

    List<Map<String,Object>> findEnableDishListInfo(String categoryId, String name);
}
