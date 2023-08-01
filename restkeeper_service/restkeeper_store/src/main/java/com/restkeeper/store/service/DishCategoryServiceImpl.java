package com.restkeeper.store.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.store.entity.DishCategory;
import com.restkeeper.store.mapper.DishCategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Slf4j
@Service(version = "1.0.0",protocol = "dubbo")
public class DishCategoryServiceImpl extends ServiceImpl<DishCategoryMapper, DishCategory> implements IDishCategoryService {

    /**
     * 分类下拉列表显示
     * @param type
     * @return
     */
    public List<Map<String, Object>> findCategoryList(Integer type) {
        QueryWrapper<DishCategory> queryWrapper = new QueryWrapper<>();

        if (type != null){
            queryWrapper.lambda().eq(DishCategory::getType,type);
        }
        queryWrapper.lambda().select(DishCategory::getCategoryId,DishCategory::getName);
        return this.listMaps(queryWrapper);
    }

    /**
     * 添加菜品
     * @param name
     * @param type
     * @return
     */
    @Transactional
    public boolean add(String name, int type) {
       //1.定义方法查验名称是否存在
       checkNameExist(name);
       DishCategory category = new DishCategory();
       category.setName(name);
       category.setType(type);
       return this.save(category);
    }

    /**
     * 分类修改
     * @param id
     * @param categoryName
     * @return
     */
    public boolean update(String id, String categoryName) {
       checkNameExist(categoryName);
       UpdateWrapper<DishCategory> wrapper = new UpdateWrapper<>();
       wrapper.lambda().eq(DishCategory::getCategoryId,id)
                       .set(DishCategory::getName,categoryName);
        return this.update(wrapper);
    }

    /**
     * 分页查询分类
     * @param pageNum
     * @param pageSize
     * @return
     */
    public IPage<DishCategory> queryPage(int pageNum, int pageSize) {
        IPage<DishCategory> page = new Page<>(pageNum,pageSize);
        QueryWrapper<DishCategory> qw = new QueryWrapper<>();
        qw.lambda()
                .orderByDesc(DishCategory::getLastUpdateTime);
        return this.page(page);
    }

    //查看分类名称是否已经存在
    private void checkNameExist(String name) {
      QueryWrapper<DishCategory> categoryQueryWrapper = new QueryWrapper<>();
      categoryQueryWrapper.lambda().select(DishCategory::getCategoryId).eq(DishCategory::getName,name);
      Integer count = this.getBaseMapper().selectCount(categoryQueryWrapper);
      if (count>0){
          throw new RuntimeException("该分类名称已存在");
      }
    }
}