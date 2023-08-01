package com.restkeeper.store.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.store.entity.SetMealDish;
import com.restkeeper.store.mapper.SetMealDishMapper;
import org.apache.dubbo.config.annotation.Service;


@org.springframework.stereotype.Service("setMealDishService")
@Service(version = "1.0.0",protocol = "dubbo")
public class SetMealDishServiceImpl extends ServiceImpl<SetMealDishMapper, SetMealDish> implements ISetMealDishService {
}
