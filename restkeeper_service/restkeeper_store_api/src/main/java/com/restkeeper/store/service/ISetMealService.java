package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.store.entity.SetMeal;
import com.restkeeper.store.entity.SetMealDish;

import java.util.List;

public interface ISetMealService extends IService<SetMeal> {

    IPage<SetMeal> queryPage(int pageNum, int pageSize, String name);

    boolean add(SetMeal setmeal, List<SetMealDish> setMealDishes);

    boolean update(SetMeal setMeal, List<SetMealDish> setMealDishes);
}
