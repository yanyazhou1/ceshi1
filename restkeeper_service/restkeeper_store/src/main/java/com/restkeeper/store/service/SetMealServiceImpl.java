package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.store.entity.SetMeal;
import com.restkeeper.store.entity.SetMealDish;
import com.restkeeper.store.mapper.SetMealMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@org.springframework.stereotype.Service("setMealService")
@Service(version = "1.0.0",protocol = "dubbo")
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, SetMeal> implements ISetMealService {

    @Autowired
    @Qualifier("setMealDishService")
    private ISetMealDishService setMealDishService;


    /**
     * 分页查询套餐列表
     * @param pageNum
     * @param pageSize
     * @param name
     * @return
     */
    public IPage<SetMeal> queryPage(int pageNum, int pageSize, String name) {
        IPage<SetMeal> page = new Page<>(pageNum,pageSize);
        QueryWrapper<SetMeal> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(name)){
            queryWrapper.lambda().like(SetMeal::getName,name);
        }
        return this.page(page,queryWrapper);
    }

    /**
     * 新增套餐
     * @param setmeal
     * @param setMealDishes
     * @return
     */
    @Override
    @Transactional
    public boolean add(SetMeal setmeal, List<SetMealDish> setMealDishes) {
        this.save(setmeal);
        setMealDishes.forEach(s->{
            s.setSetMealId(setmeal.getId());
            s.setIndex(0);
        });
        return setMealDishService.saveBatch(setMealDishes);
    }

    /**
     * 套餐编辑
     * @param setMeal
     * @param setMealDishes
     * @return
     */
    @Transactional
    public boolean update(SetMeal setMeal, List<SetMealDish> setMealDishes) {
        try {
            //修改套餐基础信息
            this.updateById(setMeal);

            //删除原有的菜品关联关系
            if (setMealDishes != null || setMealDishes.size() > 0) {

                QueryWrapper<SetMealDish> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(SetMealDish::getSetMealId, setMeal.getId());
                setMealDishService.remove(queryWrapper);

                //重建菜品的关联关系
                setMealDishes.forEach((setMealDish) -> {
                    setMealDish.setSetMealId(setMeal.getId());
                });

                setMealDishService.saveBatch(setMealDishes);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
