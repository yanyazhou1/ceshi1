package com.restkeeper.controller.store;


import com.restkeeper.response.vo.PageVO;
import com.restkeeper.store.entity.DishCategory;
import com.restkeeper.store.service.IDishCategoryService;
import com.restkeeper.vo.store.AddDishCategoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Api(tags = "分类管理")
@Slf4j
@RestController
@RequestMapping("/category")
public class DishCategoryController {

    @Reference(version = "1.0.0",check = false)
    private IDishCategoryService dishCategoryService;


    /**
     * 菜品，套餐分类下拉列表使用
     * 1 菜品 2 套餐
     * @return
     */
    @ApiOperation(value = "添加分类")
    @GetMapping("/type/{type}")
    public List<Map<String,Object>> getByType(@PathVariable Integer type){
        return dishCategoryService.findCategoryList(type);
    }


    /**
     * 分页
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "page", value = "当前页码", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "path", name = "pageSize", value = "分页大小", required = true, dataType = "Integer")})
    @GetMapping("/pageList/{page}/{pageSize}")
    public PageVO<DishCategory> findByPage(@PathVariable Integer page, @PathVariable Integer pageSize){
        return new PageVO<>(dishCategoryService.queryPage(page,pageSize));
    }

    /**
     * 分类添加
     */
    @ApiOperation(value = "分类添加")
    @PostMapping("/add")
    public boolean add(@RequestBody AddDishCategoryVO categoryVO){
        return this.dishCategoryService.add(categoryVO.getName(),categoryVO.getType());
    }

    /**
     * 分类修改
     */
    @ApiOperation(value = "分类修改")
    @PutMapping("/update/{id}")
    public boolean update(@PathVariable String id,
                          @RequestParam(name="categoryName") String categoryName){
        return this.dishCategoryService.update(id,categoryName);
    }

}
